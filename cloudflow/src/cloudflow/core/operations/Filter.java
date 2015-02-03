package cloudflow.core.operations;

import cloudflow.core.records.Record;

public abstract class Filter<IN extends Record<?, ?>> extends MapOperation<IN, IN> {

	public Filter(Class<IN> recordClass) {
		super(recordClass, recordClass);
	}

	@Override
	public void process(IN record) {
		if (!filter(record)) {
			emit(record);
		}
	}

	public abstract boolean filter(IN record);

}