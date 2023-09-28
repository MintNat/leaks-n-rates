package com.solo.leaksnrates;

import java.util.Objects;

public class UserAppID {
        private final String uuid;
        private final String appID;

        UserAppID(String uuid, String appID) {
            this.uuid = uuid;
            this.appID = appID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserAppID userApp = (UserAppID) o;

            if (!Objects.equals(uuid, userApp.uuid)) return false;
            return Objects.equals(appID, userApp.appID);
        }

        @Override
        public int hashCode() {
            int result = uuid != null ? uuid.hashCode() : 0;
            result = 31 * result + (appID != null ? appID.hashCode() : 0);
            return result;
        }

    @Override
    public String toString() {
        return "user " + uuid + " " + appID;
    }
}
