package com.example.downloadpdf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.mbms.DownloadRequest;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    //

    Button pdf_dwn;

    Uri uri = Uri.parse("https://engineering.futureuniversity.com/BOOKS%20FOR%20IT/Software-Engineering-9th-Edition-by-Ian-Sommerville.pdf");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

        pdf_dwn = findViewById(R.id.btn_download);

        pdf_dwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Downloadpdf();

            }
        });


/*
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "file.pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        long downloadId = downloadManager.enqueue(request);progressBar = findViewById(R.id.progressBar);

 */


    }

    private void Downloadpdf() {

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "file.pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        long downloadId = downloadManager.enqueue(request);

        // Register a broadcast receiver to listen for download status updates.
        DownloadBroadcastReceiver receiver = new DownloadBroadcastReceiver();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, filter);
    }


    private class DownloadBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            int status = 0,totalBytes=0,bytesDownloaded=0;

            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);

            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (columnIndex >= 0) {
                    status = cursor.getInt(columnIndex);
                }

                columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                if (columnIndex >= 0) {
                     totalBytes = cursor.getInt(columnIndex);
                }

                columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                if (columnIndex >= 0) {
                     bytesDownloaded = cursor.getInt(columnIndex);
                }

                // Update the progress bar with the percentage of the download that is complete.
                progressBar.setProgress((int) ((bytesDownloaded / (float) totalBytes) * 100));
            }

            switch (status) {
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(MainActivity.this, "Download in PROGRESS", Toast.LENGTH_SHORT).show();
                    // Display a message to the user that the download is running.
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
                    // Display a message to the user that the download is complete.
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(MainActivity.this, "Download Failed", Toast.LENGTH_SHORT).show();
                    // Display a message to the user that the download failed.
                    break;
            }

            cursor.close();
        }
    }
}