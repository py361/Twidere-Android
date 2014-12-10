/*
 * Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.fragment.support;

import android.content.Context;
import android.os.Bundle;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.mariotaku.twidere.adapter.ParcelableStatusesAdapter;
import org.mariotaku.twidere.adapter.iface.IStatusesAdapter;
import org.mariotaku.twidere.app.TwidereApplication;
import org.mariotaku.twidere.model.ParcelableStatus;
import org.mariotaku.twidere.util.message.FavoriteCreatedEvent;
import org.mariotaku.twidere.util.message.FavoriteDestroyedEvent;
import org.mariotaku.twidere.util.message.StatusDestroyedEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mariotaku on 14/12/3.
 */
public abstract class ParcelableStatusesFragment extends AbsStatusesFragment<List<ParcelableStatus>> {

    private final StatusesBusCallback mStatusesBusCallback;

    protected ParcelableStatusesFragment() {
        mStatusesBusCallback = new StatusesBusCallback(this);
    }

    public final void deleteStatus(final long statusId) {
        final List<ParcelableStatus> data = getAdapterData();
        if (statusId <= 0 || data == null) return;
        final Set<ParcelableStatus> dataToRemove = new HashSet<>();
        for (final ParcelableStatus status : data) {
            if (status.id == statusId || status.retweet_id > 0 && status.retweet_id == statusId) {
                dataToRemove.add(status);
            }
        }
        data.removeAll(dataToRemove);
        setAdapterData(data);
    }

    @Override
    public int getStatuses(long[] accountIds, final long[] maxIds, final long[] sinceIds) {
        final Bundle args = new Bundle(getArguments());
        if (maxIds != null) {
            args.putLong(EXTRA_MAX_ID, maxIds[0]);
        }
        if (sinceIds != null) {
            args.putLong(EXTRA_SINCE_ID, sinceIds[0]);
        }
        getLoaderManager().restartLoader(0, args, this);
        return -1;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Bus bus = TwidereApplication.getInstance(getActivity()).getMessageBus();
        bus.register(this);
    }

    @Override
    public void onStop() {
        final Bus bus = TwidereApplication.getInstance(getActivity()).getMessageBus();
        bus.unregister(this);
        super.onStop();
    }

    @Override
    protected long[] getAccountIds() {
        return new long[]{getAccountId()};
    }

    @Override
    protected Object getMessageBusCallback() {
        return mStatusesBusCallback;
    }

    @Override
    protected ParcelableStatusesAdapter onCreateAdapter(final Context context, final boolean compact) {
        return new ParcelableStatusesAdapter(context, compact);
    }

    @Override
    protected void onLoadMoreStatuses() {
        final IStatusesAdapter<List<ParcelableStatus>> adapter = getAdapter();
        final long[] maxIds = new long[]{adapter.getStatus(adapter.getStatusCount() - 1).id};
        getStatuses(null, maxIds, null);
    }

    public final void replaceStatus(final ParcelableStatus status) {
        final List<ParcelableStatus> data = getAdapterData();
        if (status == null || data == null) return;
        for (int i = 0, j = data.size(); i < j; i++) {
            if (status.equals(data.get(i))) {
                data.set(i, status);
            }
        }
        setAdapterData(data);
    }

    @Override
    public boolean triggerRefresh() {
        final IStatusesAdapter<List<ParcelableStatus>> adapter = getAdapter();
        final long[] accountIds = getAccountIds();
        if (adapter.getStatusCount() > 0) {
            final long[] sinceIds = new long[]{adapter.getStatus(0).id};
            getStatuses(accountIds, null, sinceIds);
        } else {
            getStatuses(accountIds, null, null);
        }
        return true;
    }

    protected long getAccountId() {
        final Bundle args = getArguments();
        return args != null ? args.getLong(EXTRA_ACCOUNT_ID, -1) : -1;
    }

    protected String[] getSavedStatusesFileArgs() {
        return null;
    }

    private void updateFavoritedStatus(ParcelableStatus status) {
        final Context context = getActivity();
        if (context == null) return;
        if (status.account_id == getAccountId()) {
            replaceStatus(status);
        }
    }

    protected static class StatusesBusCallback {

        private final ParcelableStatusesFragment fragment;

        StatusesBusCallback(ParcelableStatusesFragment fragment) {
            this.fragment = fragment;
        }

        @Subscribe
        public void notifyFavoriteCreated(FavoriteCreatedEvent event) {
            fragment.updateFavoritedStatus(event.status);
        }

        @Subscribe
        public void notifyFavoriteDestroyed(FavoriteDestroyedEvent event) {
            fragment.updateFavoritedStatus(event.status);
        }

        @Subscribe
        public void notifyStatusDestroyed(StatusDestroyedEvent event) {
            fragment.deleteStatus(event.status.id);
        }

    }

}
