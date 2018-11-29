package com.blackcard.logan.collector;

import android.os.Handler;
import android.os.Looper;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文件下载工具类
 */

public class DownloadUtil {
    private Call call;
    private DownloadProgress listener;
    private String url;
    private String fileDir;
    private String fileName;
    //允许进度重复 默认不重复
    private boolean repeat;

    //任务取消
    public DownloadUtil cancel() {
        if (call != null && !call.isCanceled()) {
            listener.onCancel();
            call.cancel();
        }
        return this;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }


    /**
     * @param url      下载连接
     * @param fileDir  下载的文件储存目录
     * @param fileName 下载文件名称
     * @param listener 下载监听
     */

    public DownloadUtil download(final String url, final String fileDir, final String fileName
            , final DownloadProgress listener) {
        this.url = url;
        this.fileDir = fileDir;
        this.fileName = fileName;
        this.listener = listener;
        new Thread() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                //异步请求
                call = new OkHttpClient().newCall(request);
                // 开始下载
                handler.post(listener::onStart);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // 下载失败监听回调
                        handler.post(() -> listener.onFailure(e));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;

                        //储存下载文件的目录
                        File dir = new File(fileDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, fileName);
                        try {
                            is = response.body().byteStream();
                            long total = response.body().contentLength();
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            int percentage = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                sum += len;
                                int progress = (int) (sum * 1.0f / total * 100);
                                //下载中更新进度条
                                long read = sum;
                                if (!repeat && percentage == progress) continue;
                                percentage = progress;
                                handler.post(() -> listener.onProgressChanged(read, total, progress));
                            }
                            fos.flush();
                            //下载完成
                            handler.postDelayed(() -> listener.onCompleted(file), 500);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //下载失败
                            handler.post(() -> listener.onFailure(e));
                        } finally {
                            try {
                                if (is != null) {
                                    is.close();
                                }
                                if (fos != null) {
                                    fos.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }.start();
        return this;
    }

    //重新开始
    public void Restart() {
        if (call != null) {
            call.cancel();
            download(url, fileDir, fileName, listener);
        }
    }

    /**
     * 进度监听
     */
    public static abstract class DownloadProgress {
        /**
         * 下载进度
         *
         * @param read          已下载数据
         * @param contentLength 总数据
         * @param percentage    百分比
         */
        public void onProgressChanged(long read, long contentLength, int percentage) {
        }

        /**
         * 开始下载
         */
        public void onStart() {
        }

        /**
         * 下载完成
         */
        public abstract void onCompleted(File file);

        /**
         * 下载失败
         */
        public void onFailure(Exception e) {
            e.printStackTrace();
        }

        /**
         * 任务取消
         */
        public void onCancel() {
        }
    }
}