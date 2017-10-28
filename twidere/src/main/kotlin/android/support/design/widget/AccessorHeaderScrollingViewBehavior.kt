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

package android.support.design.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View


internal abstract class AccessorHeaderScrollingViewBehavior(context: Context, attrs: AttributeSet? = null) : HeaderScrollingViewBehavior(context, attrs) {
    internal val tempRect1 = mTempRect1
    internal val tempRect2 = mTempRect2

    internal override fun getOverlapRatioForOffset(header: View): Float {
        return super.getOverlapRatioForOffset(header)
    }

    internal override fun findFirstDependency(views: List<View>): View? {
        throw UnsupportedOperationException()
    }

    internal override fun getScrollRange(v: View): Int {
        return super.getScrollRange(v)
    }

    internal fun getOverlapPixelsForOffsetAccessor(header: View): Int {
        return getOverlapPixelsForOffset(header)
    }
}