package de.tobiaserthal.akgbensheim.data.sync;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.R;
import de.tobiaserthal.akgbensheim.data.model.ModelUtils;
import de.tobiaserthal.akgbensheim.data.model.event.EventModel;
import de.tobiaserthal.akgbensheim.data.model.news.NewsModel;
import de.tobiaserthal.akgbensheim.data.model.substitution.SubstitutionModel;
import de.tobiaserthal.akgbensheim.data.model.teacher.TeacherModel;
import de.tobiaserthal.akgbensheim.data.provider.event.EventContentValues;
import de.tobiaserthal.akgbensheim.data.provider.event.EventCursor;
import de.tobiaserthal.akgbensheim.data.provider.event.EventSelection;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsContentValues;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsCursor;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsSelection;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionContentValues;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionCursor;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionSelection;
import de.tobiaserthal.akgbensheim.data.provider.teacher.TeacherContentValues;
import de.tobiaserthal.akgbensheim.data.provider.teacher.TeacherCursor;
import de.tobiaserthal.akgbensheim.data.provider.teacher.TeacherSelection;
import de.tobiaserthal.akgbensheim.data.rest.api.ApiError;
import de.tobiaserthal.akgbensheim.data.rest.api.RestServer;
import de.tobiaserthal.akgbensheim.data.rest.model.event.EventResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.news.NewsResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.substitution.SubstitutionResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.teacher.TeacherResponse;
import retrofit.RetrofitError;

//TODO: make sync methods a generic abstract class?
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    public static final class SYNC {
        public static final String ARG = "sync_id";

        public static final int EVENTS = 0x1;
        public static final int NEWS = 0x2;
        public static final int SUBSTITUTIONS = 0x4;
        public static final int TEACHERS = 0x8;

        public static final int ALL = 0xE;
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
        Log.d(TAG, "Beginning sync with options: %s", extras.toString());
        try {
            ArrayList<ContentProviderOperation> batchList = new ArrayList<>();

            int which = extras.getInt(SYNC.ARG, SYNC.ALL);
            if((which & SYNC.NEWS) == SYNC.NEWS) { syncNews(provider, batchList, syncResult, 0, 1); }
            if((which & SYNC.EVENTS) == SYNC.EVENTS) { syncEvents(provider, batchList, syncResult); }
            if((which & SYNC.TEACHERS) == SYNC.TEACHERS) { syncTeachers(provider, batchList, syncResult); }
            if((which & SYNC.SUBSTITUTIONS) == SYNC.SUBSTITUTIONS) { syncSubstitutions(provider, batchList, syncResult); }

            Log.i(TAG, "Merge solution ready. Applying batch update to database...");
            provider.applyBatch(batchList);

        } catch (RetrofitError e) {
            Log.e(TAG, e, "Error while trying to parse response!");
            syncResult.stats.numIoExceptions ++;
        } catch (RemoteException e) {
            Log.e(TAG, e, "Error while trying to insert into database!");
            syncResult.databaseError = true;
        } catch (ApiError e) {
            Log.e(TAG, e, "Response object returned from server invalid!");
            syncResult.stats.numParseExceptions ++;
        } catch (OperationApplicationException e) {
            Log.e(TAG, e, "Failed to send operations to content provider!");
            syncResult.databaseError = true;
        } finally {
            Log.i(TAG, "Finished syncing: %s", syncResult.toString());

            if(syncResult.madeSomeProgress()) {
                PendingIntent intent;
                try {
                    Class<?> pendingClass = Class.forName("de.tobiaserthal.akgbensheim.MainActivity");
                    Intent activity = new Intent(getContext(), pendingClass);

                    intent = PendingIntent.getActivity(
                            getContext(), 0, activity, PendingIntent.FLAG_UPDATE_CURRENT);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();

                    Intent activity = new Intent();
                    intent = PendingIntent.getActivity(
                            getContext(), -1, activity, PendingIntent.FLAG_UPDATE_CURRENT);
                }

                int changes = (int) syncResult.stats.numDeletes
                        + (int) syncResult.stats.numUpdates
                        + (int) syncResult.stats.numInserts;

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(getContext().getString(R.string.notify_subst_changed_title))
                                .setContentText(getContext().getString(R.string.notify_subst_changed_content, changes))
                                .setContentIntent(intent);

                NotificationManagerCompat.from(getContext()).notify(1, builder.build());
            }
        }
    }

    private void syncEvents(ContentProviderClient provider, ArrayList<ContentProviderOperation> batch,
                            SyncResult syncResult) throws ApiError, RemoteException, OperationApplicationException {
        Log.i(TAG, "Computing update solution for events table...");

        Log.i(TAG, "Parsing results from rest server...");
        List<EventResponse> entries = RestServer.getEvents();

        Log.i(TAG, "Parsed %d entries. Mapping fetched data...", entries.size());
        HashMap<Long, EventModel> entryMap = new HashMap<>();
        for(EventResponse response : entries) {
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
                          SyncResult syncResult, int start, int numOfSites) throws ApiError, RemoteException, OperationApplicationException {
        Log.i(TAG, "Computing update solution for news table...");

        Log.i(TAG, "Parsing results from rest server..."); // TODO: check
        List<NewsResponse> entries = new ArrayList<>();
        for(int i = 0; i < numOfSites; i ++) {
            entries.addAll(RestServer.getNews(start + i * 10));
        }

        Log.i(TAG, "Parsed %d entries. Mapping fetched data...", entries.size());
        HashMap<Long, NewsModel> entryMap = new HashMap<>();
        for(NewsResponse response : entries) {
            entryMap.put(response.getId(), response);
        }

        Log.i(TAG, "Fetching local database...");
        NewsSelection query = new NewsSelection();
        NewsCursor cursor = NewsCursor.wrap(
                provider.query(query.uri(), null, query.sel(), query.args(), query.order())); // TODO: maybe query a limit?

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
                                   SyncResult syncResult) throws RemoteException, ApiError, OperationApplicationException {
        Log.i(TAG, "Computing update solution for substitutions table...");


        Log.i(TAG, "Parsing results from rest server...");
        List<SubstitutionResponse> entries = RestServer.getSubstitutions();

        Log.i(TAG, "Parsed %d entries. Mapping fetched data...", entries.size());
        HashMap<Long, SubstitutionModel> entryMap = new HashMap<>();
        for(SubstitutionResponse response : entries) {
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

            if(match != null) {
                entryMap.remove(id);

                if(!ModelUtils.equal(match, cursor)) {
                    Log.i(TAG, "Scheduling update for: %s/%s", entry.uri().toString(), String.valueOf(id));
                    batch.add(ContentProviderOperation.newUpdate(entry.uri())
                            .withSelection(entry.sel(), entry.args())
                            .withValues(SubstitutionContentValues.wrap(match).values())
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

        for(SubstitutionModel model : entryMap.values()) {
            SubstitutionContentValues values = SubstitutionContentValues.wrap(model);

            Log.i(TAG, "Scheduling insert for: %s/%s", values.uri(), String.valueOf(model.getId()));
            batch.add(ContentProviderOperation.newInsert(values.uri())
                    .withValues(values.values())
                    .build());
            syncResult.stats.numInserts ++;
        }

        Log.i(TAG, "Finished adding update solution for substitution table.");
    }

    private void syncTeachers(ContentProviderClient provider, ArrayList<ContentProviderOperation> batch,
                              SyncResult syncResult) throws RemoteException, ApiError, OperationApplicationException {
        Log.i(TAG, "Computing update solution for teachers table...");

        Log.i(TAG, "Parsing results from rest server...");
        List<TeacherResponse> entries = RestServer.getTeachers();

        Log.i(TAG, "Parsed %d entries. Mapping fetched data...", entries.size());
        HashMap<Long, TeacherModel> entryMap = new HashMap<>();
        for(TeacherModel response : entries) {
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
