package de.tobiaserthal.akgbensheim.backend.sync;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.tobiaserthal.akgbensheim.backend.model.ModelUtils;
import de.tobiaserthal.akgbensheim.backend.model.event.EventModel;
import de.tobiaserthal.akgbensheim.backend.model.news.NewsModel;
import de.tobiaserthal.akgbensheim.backend.model.substitution.SubstitutionModel;
import de.tobiaserthal.akgbensheim.backend.model.teacher.TeacherModel;
import de.tobiaserthal.akgbensheim.backend.provider.event.EventContentValues;
import de.tobiaserthal.akgbensheim.backend.provider.event.EventCursor;
import de.tobiaserthal.akgbensheim.backend.provider.event.EventSelection;
import de.tobiaserthal.akgbensheim.backend.provider.news.NewsColumns;
import de.tobiaserthal.akgbensheim.backend.provider.news.NewsContentValues;
import de.tobiaserthal.akgbensheim.backend.provider.news.NewsCursor;
import de.tobiaserthal.akgbensheim.backend.provider.news.NewsSelection;
import de.tobiaserthal.akgbensheim.backend.provider.substitution.SubstitutionContentValues;
import de.tobiaserthal.akgbensheim.backend.provider.substitution.SubstitutionCursor;
import de.tobiaserthal.akgbensheim.backend.provider.substitution.SubstitutionSelection;
import de.tobiaserthal.akgbensheim.backend.provider.teacher.TeacherContentValues;
import de.tobiaserthal.akgbensheim.backend.provider.teacher.TeacherCursor;
import de.tobiaserthal.akgbensheim.backend.provider.teacher.TeacherSelection;
import de.tobiaserthal.akgbensheim.backend.rest.api.ApiError;
import de.tobiaserthal.akgbensheim.backend.rest.api.RestServer;
import de.tobiaserthal.akgbensheim.backend.rest.model.event.EventResponse;
import de.tobiaserthal.akgbensheim.backend.rest.model.news.NewsKeys;
import de.tobiaserthal.akgbensheim.backend.rest.model.news.NewsResponse;
import de.tobiaserthal.akgbensheim.backend.rest.model.substitution.SubstitutionResponse;
import de.tobiaserthal.akgbensheim.backend.rest.model.teacher.TeacherResponse;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.backend.utils.NetworkManager;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    public static final class ARGS {
        public static final String ID = "sync_id";
        public static final String NEWS_START = "sync_extra_newsStart";
        public static final String NEWS_COUNT = "sync_extra_newsCount";
    }

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (!NetworkManager.getInstance().isAccessAllowed()) {
            Log.w(TAG, "Not allowed to start sync due to network settings! Aborting...");
            //syncResult.stats.numIoExceptions ++; // FIXME: might be critical to the adapter pending issue
            return;
        }

        Log.d(TAG, "Beginning sync with options: %s", extras.toString());
        try {
            ArrayList<ContentProviderOperation> batchList = new ArrayList<>();

            int which = extras.getInt(ARGS.ID, ModelUtils.ALL);
            if((which & ModelUtils.NEWS) == ModelUtils.NEWS) {
                syncNews(provider, batchList, syncResult, extras);
            }

            if((which & ModelUtils.EVENTS) == ModelUtils.EVENTS) {
                syncEvents(provider, batchList, syncResult);
            }

            if((which & ModelUtils.TEACHERS) == ModelUtils.TEACHERS) {
                syncTeachers(provider, batchList, syncResult);
            }

            if((which & ModelUtils.SUBSTITUTIONS) == ModelUtils.SUBSTITUTIONS) {
                syncSubstitutions(provider, batchList, syncResult);
            }

            Log.i(TAG, "Merge solution ready. Applying batch update to database...");
            provider.applyBatch(batchList);

        } catch (IOException e) {
            Log.e(TAG, e, "Error while trying to parse response!");
            syncResult.stats.numIoExceptions++; // FIXME: might be critical to the adapter pending issue
        } catch (ApiError e) {
            Log.e(TAG, e, "Response object returned from server invalid!");
            syncResult.stats.numParseExceptions ++;
        } catch (RemoteException e) {
            Log.e(TAG, e, "Error while trying to insert into database!");
            syncResult.databaseError = true;
        } catch (OperationApplicationException e) {
            Log.e(TAG, e, "Failed to send operations to content provider!");
            syncResult.databaseError = true;
        } finally {
            Log.i(TAG, "Finished syncing: %s", syncResult.toString());
        }
    }

    private void syncEvents(ContentProviderClient provider, ArrayList<ContentProviderOperation> batch,
                           SyncResult syncResult) throws ApiError, RemoteException, OperationApplicationException, IOException {
        Log.d(TAG, "Computing update solution for events table...");

        Log.i(TAG, "Parsing results from rest server...");
        List<EventResponse.Entry> entries = RestServer.getInstance().getEvents();

        Log.i(TAG, "Parsed %d entries. Mapping fetched data...", entries.size());
        HashMap<Long, EventModel> entryMap = new HashMap<>();
        for(EventResponse.Entry response : entries) {
            entryMap.put(response.getId(), response);
        }

        Log.i(TAG, "Fetching local database...");
        EventSelection query = new EventSelection();
        EventCursor cursor = EventCursor.wrap(
                provider.query(query.uri(), null, query.sel(), query.args(), query.order()));

        Log.i(TAG, "Fetched %d entries from database. Computing merge solution...", cursor.getCount());
        while (cursor.moveToNext()) {
            long id = cursor.getId();
            syncResult.stats.numEntries ++;

            EventModel match = entryMap.get(id);
            EventSelection entry = new EventSelection().id(id);

            if(match != null) {
                entryMap.remove(id);

                if(!ModelUtils.equal(match, cursor)) {
                    Log.i(TAG, "Scheduling update for: %s/%s", entry.uri().toString(), String.valueOf(id));
                    batch.add(ContentProviderOperation.newUpdate(entry.uri())
                            .withSelection(entry.sel(), entry.args())
                            .withValues(EventContentValues.wrap(match).values())
                            .build());
                    syncResult.stats.numUpdates ++;
                } else {
                    Log.i(TAG, "No action for: %s/%s", entry.uri().toString(), String.valueOf(id));
                    syncResult.stats.numSkippedEntries ++;
                }
            } else {
                Log.i(TAG, "Scheduling delete for: %s/%s", entry.uri().toString(), String.valueOf(id));
                batch.add(ContentProviderOperation.newDelete(entry.uri())
                        .withSelection(entry.sel(), entry.args())
                        .build());
                syncResult.stats.numDeletes ++;
            }
        }
        cursor.close();

        for(EventModel model : entryMap.values()) {
            EventContentValues values = EventContentValues.wrap(model);

            Log.i(TAG, "Scheduling insert for: %s/%s", values.uri(), String.valueOf(model.getId()));
            batch.add(ContentProviderOperation.newInsert(values.uri())
                    .withValues(values.values())
                    .build());

            syncResult.stats.numInserts ++;
        }

        Log.i(TAG, "Finished adding update solution for event table.");
    }

    private void syncNews(ContentProviderClient provider, ArrayList<ContentProviderOperation> batch,
                         SyncResult syncResult, Bundle extras) throws ApiError, RemoteException, OperationApplicationException, IOException {
        Log.d(TAG, "Computing update solution for news table...");

        int start = Math.max(extras.getInt(ARGS.NEWS_START, 0), 0);
        int count = Math.max(extras.getInt(ARGS.NEWS_COUNT, 0), 0);

        // this means we should either refresh everything or fire up an initial sync
        if(count == 0) {
            NewsSelection selection = NewsSelection.getAll();
            String[] projection = {"count(" + NewsColumns._ID + ")"};

            Cursor cursor = provider.query(
                    selection.uri(),
                    projection,
                    selection.sel(),
                    selection.args(),
                    selection.order()
            );

            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    // refresh at least the first 10 entries
                    count = Math.max(cursor.getInt(0), NewsKeys.ITEMS_PER_PAGE);
                }

                cursor.close();
            }
        }

        Log.i(TAG, "Parsing results from rest server with start: %d and count: %d...", start, count);
        List<NewsResponse.Entry> entries = RestServer.getInstance().getNews(start, count);

        Log.i(TAG, "Parsed %d entries. Mapping fetched data...", entries.size());
        HashMap<Long, NewsModel> entryMap = new HashMap<>();
        for(NewsResponse.Entry response : entries) {
            entryMap.put(response.getId(), response);
        }

        Log.i(TAG, "Fetching local database with offset: %d and limit: %d...", start, count);
        NewsSelection query = NewsSelection.getAll()
                .limit(count)
                .offset(start);

        NewsCursor cursor = NewsCursor.wrap(
                provider.query(query.uri(), null, query.sel(), query.args(), query.order()));

        Log.i(TAG, "Fetched %d entries from database. Computing merge solution...", cursor.getCount());
        while (cursor.moveToNext()) {
            long id = cursor.getId();
            syncResult.stats.numEntries ++;

            NewsModel match = entryMap.get(id);
            NewsSelection entry = new NewsSelection().id(id);

            if(match != null) {
                entryMap.remove(id);

                if(!ModelUtils.equal(match, cursor)) {
                    Log.i(TAG, "Scheduling update for: %s/%s", entry.uri().toString(), String.valueOf(id));
                    batch.add(ContentProviderOperation.newUpdate(entry.uri())
                            .withSelection(entry.sel(), entry.args())
                            .withValues(NewsContentValues.wrap(match).values())
                            .build());
                    syncResult.stats.numUpdates ++;
                } else {
                    Log.i(TAG, "No action for: %s/%s", entry.uri().toString(), String.valueOf(id));
                    syncResult.stats.numSkippedEntries ++;
                }

            } else {
                Log.i(TAG, "Scheduling delete for: %s/%s", entry.uri().toString(), String.valueOf(id));
                batch.add(ContentProviderOperation.newDelete(entry.uri())
                        .withSelection(entry.sel(), entry.args())
                        .build());
                syncResult.stats.numDeletes ++;
            }
        }
        cursor.close();

        for(NewsModel model : entryMap.values()) {
            NewsContentValues values = NewsContentValues.wrap(model);

            Log.i(TAG, "Scheduling insert for: %s/%s", values.uri(), String.valueOf(model.getId()));
            batch.add(ContentProviderOperation.newInsert(values.uri())
                    .withValues(values.values())
                    .build());
            syncResult.stats.numInserts ++;
        }

        Log.i(TAG, "Finished adding update solution for news table.");
    }

    private void syncSubstitutions(ContentProviderClient provider, ArrayList<ContentProviderOperation> batch,
                                   SyncResult syncResult) throws RemoteException, ApiError, OperationApplicationException, IOException {
        Log.d(TAG, "Computing update solution for substitutions table...");


        Log.i(TAG, "Parsing results from rest server...");
        List<SubstitutionResponse.Entry> entries = RestServer.getInstance().getSubstitutions();

        Log.i(TAG, "Parsed %d entries. Mapping fetched data...", entries.size());
        HashMap<Long, SubstitutionModel> entryMap = new HashMap<>();
        for(SubstitutionResponse.Entry response : entries) {
            entryMap.put(response.getId(), response);
        }

        Log.i(TAG, "Fetching local database...");
        SubstitutionSelection query = new SubstitutionSelection();
        SubstitutionCursor cursor = SubstitutionCursor.wrap(
                provider.query(query.uri(), null, query.sel(), query.args(), query.order()));

        Log.i(TAG, "Fetched %d entries from database. Computing merge solution...", cursor.getCount());
        while (cursor.moveToNext()) {
            long id = cursor.getId();
            syncResult.stats.numEntries ++;

            SubstitutionModel match = entryMap.get(id);
            SubstitutionSelection entry = new SubstitutionSelection().id(id);

            if (match != null) {
                entryMap.remove(id);

                if (!ModelUtils.equal(match, cursor)) {
                    Log.i(TAG, "Scheduling update for: %s/%s", entry.uri().toString(), String.valueOf(id));
                    batch.add(ContentProviderOperation.newUpdate(entry.uri())
                            .withSelection(entry.sel(), entry.args())
                            .withValues(SubstitutionContentValues.wrap(match).values())
                            .build());
                    syncResult.stats.numUpdates ++;
                } else {
                    Log.i(TAG, "No action for: %s/%s", entry.uri().toString(), String.valueOf(id));
                    syncResult.stats.numSkippedEntries++;
                }

            } else {
                Log.i(TAG, "Scheduling delete for: %s/%s", entry.uri().toString(), String.valueOf(id));
                batch.add(ContentProviderOperation.newDelete(entry.uri())
                        .withSelection(entry.sel(), entry.args())
                        .build());
                syncResult.stats.numDeletes ++;
            }
        }
        cursor.close();

        for(SubstitutionModel model : entryMap.values()) {
            SubstitutionContentValues values = SubstitutionContentValues.wrap(model);

            Log.i(TAG, "Scheduling insert for: %s/%s", values.uri(), String.valueOf(model.getId()));
            batch.add(ContentProviderOperation.newInsert(values.uri())
                    .withValues(values.values())
                    .build());
            syncResult.stats.numInserts ++;
        }

        Log.i(TAG, "Finished adding update solution for substitution table");
    }

    private void syncTeachers(ContentProviderClient provider, ArrayList<ContentProviderOperation> batch,
                             SyncResult syncResult) throws RemoteException, ApiError, OperationApplicationException, IOException {
        Log.d(TAG, "Computing update solution for teachers table...");

        Log.i(TAG, "Parsing results from rest server...");
        List<TeacherResponse.Entry> entries = RestServer.getInstance().getTeachers();

        Log.i(TAG, "Parsed %d entries. Mapping fetched data...", entries.size());
        HashMap<Long, TeacherModel> entryMap = new HashMap<>();
        for(TeacherResponse.Entry response : entries) {
            entryMap.put(response.getId(), response);
        }

        Log.i(TAG, "Fetching local database...");
        TeacherSelection query = new TeacherSelection();
        TeacherCursor cursor = TeacherCursor.wrap(
                provider.query(query.uri(), null, query.sel(), query.args(), query.order()));

        Log.i(TAG, "Fetched %d entries from database. Computing merge solution...", cursor.getCount());
        while (cursor.moveToNext()) {
            long id = cursor.getId();
            syncResult.stats.numEntries ++;

            TeacherModel match = entryMap.get(id);
            TeacherSelection entry = new TeacherSelection().id(id);

            if(match != null) {
                entryMap.remove(id);

                if(!ModelUtils.equal(match, cursor)) {
                    Log.i(TAG, "Scheduling update for: %s/%s", entry.uri().toString(), String.valueOf(id));
                    batch.add(ContentProviderOperation.newUpdate(entry.uri())
                            .withSelection(entry.sel(), entry.args())
                            .withValues(TeacherContentValues.wrap(match).values())
                            .build());
                    syncResult.stats.numUpdates ++;
                } else {
                    Log.i(TAG, "No action for: %s/%s", entry.uri().toString(), String.valueOf(id));
                    syncResult.stats.numSkippedEntries ++;
                }
            } else {
                Log.i(TAG, "Scheduling delete for: %s/%s", entry.uri().toString(), String.valueOf(id));
                batch.add(ContentProviderOperation.newDelete(entry.uri())
                        .withSelection(entry.sel(), entry.args())
                        .build());
                syncResult.stats.numDeletes ++;
            }
        }
        cursor.close();

        for(TeacherModel model : entryMap.values()) {
            TeacherContentValues values = TeacherContentValues.wrap(model);

            Log.i(TAG, "Scheduling insert for: %s/%s", values.uri(), String.valueOf(model.getId()));
            batch.add(ContentProviderOperation.newInsert(values.uri())
                    .withValues(values.values())
                    .build());
            syncResult.stats.numInserts ++;
        }

        Log.i(TAG, "Finished adding update solution for teachers table.");
    }
}
