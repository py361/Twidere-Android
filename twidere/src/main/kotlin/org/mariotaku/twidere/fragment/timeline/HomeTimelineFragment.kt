/*
 *             Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2017 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package org.mariotaku.twidere.fragment.timeline

import android.net.Uri
import org.mariotaku.twidere.annotation.FilterScope
import org.mariotaku.twidere.model.refresh.RefreshTaskParam
import org.mariotaku.twidere.provider.TwidereDataStore.Statuses

class HomeTimelineFragment : AbsTimelineFragment<RefreshTaskParam>() {
    override val filterScope: Int = FilterScope.HOME

    override val contentUri: Uri = Statuses.CONTENT_URI

    override fun onCreateRefreshParam(position: Int): RefreshTaskParam {
        return getBaseRefreshTaskParam(this, position)
    }

    override fun getStatuses(param: RefreshTaskParam): Boolean {
        if (!param.hasMaxIds) return twitterWrapper.refreshAll(param.accountKeys)
        return twitterWrapper.getHomeTimelineAsync(param)
    }
}