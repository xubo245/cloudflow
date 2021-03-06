package cloudflow.examples;

import java.io.IOException;

import cloudflow.bio.BioPipeline;
import cloudflow.bio.ChunkSize;
import cloudflow.bio.vcf.VcfChunk;
import cloudflow.core.hadoop.MapReduceRunner;
import cloudflow.core.operations.Summarizer;
import cloudflow.core.records.GroupedRecords;
import cloudflow.core.records.TextRecord;

public class VcfChunkTest {

	static public class ChunkInfos extends Summarizer<VcfChunk, TextRecord> {

		private TextRecord info = new TextRecord();

		public ChunkInfos() {
			super(VcfChunk.class, TextRecord.class);
		}

		@Override
		public void summarize(String key, GroupedRecords<VcfChunk> values) {
			int noSnps = 0;
			while (values.hasNextRecord()) {
				noSnps++;
				// consume!!
				values.getRecord();
			}

			info.setKey(key);
			info.setValue(noSnps + " SNPS in chunk.");

			emit(info);

		}

	}

	public static void main(String[] args) throws IOException {

		String input = args[0];
		String output = args[1];

		BioPipeline pipeline = new BioPipeline("VCF Chunk test",
				VcfChunkTest.class);

		pipeline.loadVcf(input).split(1, ChunkSize.MBASES)
				.apply(ChunkInfos.class).save(output);

		boolean result = new MapReduceRunner().run(pipeline);
		if (!result) {
			System.exit(1);
		}
	}
}
