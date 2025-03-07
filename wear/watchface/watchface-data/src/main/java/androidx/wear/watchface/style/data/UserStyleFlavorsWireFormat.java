/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.wear.watchface.style.data;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.versionedparcelable.ParcelField;
import androidx.versionedparcelable.ParcelUtils;
import androidx.versionedparcelable.VersionedParcelable;
import androidx.versionedparcelable.VersionedParcelize;

import java.util.ArrayList;
import java.util.List;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@VersionedParcelize
@SuppressLint("BanParcelableUsage") // TODO(b/169214666): Remove Parcelable
public class UserStyleFlavorsWireFormat implements VersionedParcelable, Parcelable {
    @ParcelField(1)
    @NonNull
    public List<UserStyleFlavorWireFormat> mFlavors = new ArrayList<>();

    UserStyleFlavorsWireFormat() {}

    public UserStyleFlavorsWireFormat(@NonNull List<UserStyleFlavorWireFormat> flavors) {
        mFlavors = flavors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /** Serializes this UserStyleSchemaWireFormat to the specified {@link Parcel}. */
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeParcelable(ParcelUtils.toParcelable(this), flags);
    }

    public static final Creator<UserStyleFlavorsWireFormat> CREATOR =
            new Creator<UserStyleFlavorsWireFormat>() {
                @SuppressWarnings("deprecation")
                @Override
                public UserStyleFlavorsWireFormat createFromParcel(Parcel source) {
                    return ParcelUtils.fromParcelable(
                            source.readParcelable(getClass().getClassLoader()));
                }

                @Override
                public UserStyleFlavorsWireFormat[] newArray(int size) {
                    return new UserStyleFlavorsWireFormat[size];
                }
            };
}
