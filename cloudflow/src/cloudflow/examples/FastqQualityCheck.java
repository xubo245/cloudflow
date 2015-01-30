package cloudflow.examples;

import java.io.IOException;

import org.apache.hadoop.io.Text;

import cloudflow.bio.BioPipeline;
import cloudflow.bio.fastq.FastqRecord;
import cloudflow.core.operations.MapStep;
import cloudflow.core.records.IntIntRecord;

public class FastqQualityCheck {

	static public class SplitByPos extends MapStep<FastqRecord, IntIntRecord> {

		IntIntRecord outRecord = new IntIntRecord();

		public SplitByPos() {
			super(FastqRecord.class, IntIntRecord.class);
		}

		@Override
		public void process(FastqRecord record) {

			Text qualities = record.getValue().getQuality();

			for (int pos = 0; pos < qualities.getLength(); pos++) {
				outRecord.setKey(pos);
				outRecord.setValue(qualities.charAt(pos));
				emit(outRecord);
			}

		}

	}

	public static void main(String[] args) throws IOException {

		String input = args[0];
		String output = args[1];

		BioPipeline pipeline = new BioPipeline("Bam Quality Check",
				FastqQualityCheck.class);

		pipeline.loadFastq(input).apply(SplitByPos.class).mean().save(output);

		boolean result = pipeline.run();
		if (!result) {
			System.exit(1);
		}
	}
}
