package com.needayeah.elastic.common;


import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
public class Record implements Cloneable {
    @Getter
    @Setter
    private Pair<List<BizRecord>, List<BizRecord>> bizRecordPair;

    public Record recordSuccess(List<String> subjectIds, @Nullable Supplier<String> reasonSupplier) {
        if(bizRecordPair == null) {
            newRecordPair();
        }
        buildBizRecord(subjectIds, reasonSupplier, bizRecordPair.getLeft());
        return this;
    }

    public Record recordSuccess(String subjectId) {
        return this.recordSuccess(Collections.singletonList(subjectId), null);
    }

    public Record recordFailure(String subjectId) {
        return this.recordFailure(Collections.singletonList(subjectId), null);
    }

    public Record recordSuccess(List<String> subjectIds) {
        return this.recordSuccess(subjectIds, null);
    }

    public Record recordFailure(List<String> subjectIds) {
        return this.recordFailure(subjectIds, null);
    }


    public Record recordSuccess(String subjectId, @Nullable Supplier<String> reasonSupplier) {
        return this.recordSuccess(Collections.singletonList(subjectId), reasonSupplier);
    }

    public Record recordSuccess(String subjectId, String operationDesc) {
        if(bizRecordPair == null) {
            newRecordPair();
        }
        BizRecord record = BizRecord.builder()
                .subjectId(subjectId)
                .operation(operationDesc)
                .build();
        bizRecordPair.getLeft().add(record);
        return this;
    }

    public Record recordFailure(String subjectId, String operationDesc) {
        if(bizRecordPair == null) {
            newRecordPair();
        }
        BizRecord record = BizRecord.builder()
                .subjectId(subjectId)
                .operation(operationDesc)
                .build();
        bizRecordPair.getRight().add(record);
        return this;
    }

    public Record recordFailure(String subjectId, @Nullable Supplier<String> reasonSupplier) {
        return this.recordFailure(Collections.singletonList(subjectId), reasonSupplier);
    }

    private synchronized void newRecordPair() {
        this.bizRecordPair = Pair.of(Lists.newArrayList(), Lists.newArrayList());
    }

    public Record recordFailure(List<String> subjectIds, @Nullable Supplier<String> reasonSupplier) {
        if(bizRecordPair == null) {
            newRecordPair();
        }
        buildBizRecord(subjectIds, reasonSupplier, bizRecordPair.getRight());
        return this;
    }

    private void buildBizRecord(List<String> subjectIds, Supplier<String> reasonSupplier, List<BizRecord> records) {
        for(String subjectId : subjectIds) {
            BizRecord record = BizRecord.builder()
                    .subjectId(subjectId)
                    .operation(reasonSupplier == null ? null : reasonSupplier.get())
                    .build();
            records.add(record);
        }
    }


    @Override
    public Record clone()  {
        Record record = new Record();
        record.setBizRecordPair(this.bizRecordPair);
        return record;
    }
}