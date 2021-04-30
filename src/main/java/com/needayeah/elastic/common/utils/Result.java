package com.needayeah.elastic.common.utils;

import com.needayeah.elastic.common.log.Record;
import com.needayeah.elastic.common.page.Page;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
public class Result<T> implements Serializable {
    private Record record;
    private int status = 200;
    private String message = "success";
    private T data;

    public Record newRecord() {
        this.record = new Record();
        return this.record;
    }

    public Result() {
    }

    public Result(T data) {
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result(data);
    }

    public static <T> Result<T> unKnowErr(String message, Object... args) {
        return error(5000, message, args);
    }

    public static <T> Result<T> error(Integer code, String errMsg, Object... args) {
        Object[] var3 = args;
        int var4 = args.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Object arg = var3[var5];
            errMsg = errMsg.replaceFirst("\\{\\}", Objects.toString(arg));
        }

        return new Result(code, errMsg);
    }

    public static <T> Result<T> error(Integer code, String errMsg) {
        return new Result(code, errMsg);
    }

    public static <T> Result<T> error(Result result) {
        return new Result(result.getStatus(), result.getMessage());
    }

    public Result(int status, String message) {
        this.setStatus(status);
        this.message = message;
    }

    public Result(int status, String message, T data) {
        this.setStatus(status);
        this.message = message;
        this.data = data;
    }

    public boolean ok() {
        return this.status == 200;
    }

    public boolean success() {
        return this.status == 200 && this.data != null;
    }

    public boolean successAndNotEmpty() {
        if (this.data instanceof Collection) {
            return this.status == 200 && !CollectionUtils.isEmpty((Collection) this.data);
        } else {
            return this.status == 200 && this.data != null;
        }
    }

    public static <T> Result<Page<T>> emptyPageResult() {
        return new Result(new Page(0, Collections.emptyList()));
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Record getRecord() {
        return this.record;
    }

    public void setRecord(final Record record) {
        this.record = record;
    }
}
