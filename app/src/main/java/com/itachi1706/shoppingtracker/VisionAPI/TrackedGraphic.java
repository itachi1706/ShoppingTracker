/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itachi1706.shoppingtracker.VisionAPI;

/**
 * Common base class for defining graphics for a particular item type.  This along with
 * {@link GraphicTracker} avoids the need to duplicate this code for both the face and barcode
 * instances.
 *
 * Created by Kenneth on 9/3/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.VisionAPI
 */
abstract class TrackedGraphic<T> extends GraphicOverlay.Graphic {

    private int mId;

    TrackedGraphic(GraphicOverlay overlay) {
        super(overlay);
    }

    void setId(int id) {
        mId = id;
    }

    protected int getId() {
        return mId;
    }

    abstract void updateItem(T item);

}