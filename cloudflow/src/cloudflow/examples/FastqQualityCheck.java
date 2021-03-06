package cloudflow.examples;

import java.io.IOException;

import org.apache.hadoop.io.Text;

import cloudflow.bio.BioPipeline;
import cloudflow.bio.fastq.FastqRecord;
import cloudflow.core.hadoop.MapReduceRunner;
import cloudflow.core.operations.Transformer;
import cloudflow.core.records.IntegerRecord;

public class FastqQualityCheck {

	static public class SplitByPos extends Transformer<FastqRecord, IntegerRecord> {

		IntegerRecord outRecord = new IntegerRecord();

		public SplitByPos() {
			super(FastqRecord.class, IntegerRecord.class);
		}

		@Override
		public void transform(FastqRecord record) {

			Text qualities = record.getValue().getQuality();

			for (int pos = 0; pos < qualities.getLength(); pos++) {
				outRecord.setKey(pos + "");
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

		boolean result = new MapReduceRunner().run(pipeline);
		if (!result) {
			System.exit(1);
		}
	}
}
