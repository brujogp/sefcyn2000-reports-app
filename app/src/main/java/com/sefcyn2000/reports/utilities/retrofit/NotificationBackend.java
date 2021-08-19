package com.sefcyn2000.reports.utilities.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationBackend {
    @GET("notifications/reports")
    Call<Void> notifyReportsFinished(@Query("client") String clientId, @Query("report-id") String reportId);
}
