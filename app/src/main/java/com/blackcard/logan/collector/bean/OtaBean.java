package com.blackcard.logan.collector.bean;

/**
 * Created by Logan on 2018/11/12.
 */
public class OtaBean {
    /**
     * result : Sucess
     * data : null
     * data1 : {"tui_id":5,"version":"20180108","date":"2018-01-26","path":"/data/wx365/Device/","filename":"20180108.bin","size":"39746","tui_type":0,"tu_data1":null,"tu_data2":null,"tu_data3":null}
     * error_code : 0
     */

    private String result;
    private Object data;
    private Data1Bean data1;
    private int error_code;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Data1Bean getData1() {
        return data1;
    }

    public void setData1(Data1Bean data1) {
        this.data1 = data1;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public static class Data1Bean {
        /**
         * tui_id : 5
         * version : 20180108
         * date : 2018-01-26
         * path : /data/wx365/Device/
         * filename : 20180108.bin
         * size : 39746
         * tui_type : 0
         * tu_data1 : null
         * tu_data2 : null
         * tu_data3 : null
         */

        private int tui_id;
        private String version;
        private String date;
        private String path;
        private String filename;
        private String size;
        private int tui_type;
        private Object tu_data1;
        private Object tu_data2;
        private Object tu_data3;

        public int getTui_id() {
            return tui_id;
        }

        public void setTui_id(int tui_id) {
            this.tui_id = tui_id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getTui_type() {
            return tui_type;
        }

        public void setTui_type(int tui_type) {
            this.tui_type = tui_type;
        }

        public Object getTu_data1() {
            return tu_data1;
        }

        public void setTu_data1(Object tu_data1) {
            this.tu_data1 = tu_data1;
        }

        public Object getTu_data2() {
            return tu_data2;
        }

        public void setTu_data2(Object tu_data2) {
            this.tu_data2 = tu_data2;
        }

        public Object getTu_data3() {
            return tu_data3;
        }

        public void setTu_data3(Object tu_data3) {
            this.tu_data3 = tu_data3;
        }
    }
}
