//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

/*
package in.dream_lab.bm.stream_iot.storm.topo.micro;

import in.dream_lab.bm.stream_iot.storm.bolts.BaseTaskBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.AccumlatorBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.AnnotateBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.AzureBlobDownloadTaskBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.AzureBlobUploadTaskBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.AzureTableInsertBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.AzureTableTaskBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.BlockWindowAverageBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.BloomFilterCheckBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.BloomFilterTrainBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.CsvToSenMlParseBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.DecisionTreeClassifyBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.DecisionTreeTrainBatchedBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.DecisionTreeTrainBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.DistinctApproxCountBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.InterpolationBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.KalmanFilterBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.LineChartPlotBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.LinearRegressionPredictorBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.LinearRegressionTrainBatchedBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.LinearRegressionTrainBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.MQTTPublishTaskBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.MultiLineChartPlotBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.NoOperationBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.PiByVieteBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.RangeFilterCheckBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.SecondOrderMomentBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.SenMlParseBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.SimpleLinearRegressionPredictorBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.XMLParseBolt;
import in.dream_lab.bm.stream_iot.storm.bolts.AggregateBolts.ZipMultipleBufferBolt;
import java.util.Properties;

public class MicroTopologyFactory {
	public MicroTopologyFactory() {
	}

	public static BaseTaskBolt newTaskBolt(String taskName, Properties p) {
		byte var3 = -1;
		switch(taskName.hashCode()) {
			case -2033905901:
				if(taskName.equals("RangeFilterCheck")) {
					var3 = 5;
				}
				break;
			case -1988643718:
				if(taskName.equals("CsvToSenML")) {
					var3 = 17;
				}
				break;
			case -1918253228:
				if(taskName.equals("AzureBlobDownload")) {
					var3 = 6;
				}
				break;
			case -1628295132:
				if(taskName.equals("SimpleLinearRegressionPredictor")) {
					var3 = 22;
				}
				break;
			case -1440548942:
				if(taskName.equals("MultiLineChartPlot")) {
					var3 = 29;
				}
				break;
			case -1254195786:
				if(taskName.equals("LinearRegressionPredictor")) {
					var3 = 20;
				}
				break;
			case -609518921:
				if(taskName.equals("LinearRegressionTrainBatched")) {
					var3 = 12;
				}
				break;
			case -585018004:
				if(taskName.equals("Annotate")) {
					var3 = 26;
				}
				break;
			case -577246579:
				if(taskName.equals("BloomFilterCheck")) {
					var3 = 3;
				}
				break;
			case -561252467:
				if(taskName.equals("BloomFilterTrain")) {
					var3 = 4;
				}
				break;
			case -472142759:
				if(taskName.equals("DistinctApproxCount")) {
					var3 = 1;
				}
				break;
			case -72277896:
				if(taskName.equals("SenMlParse")) {
					var3 = 16;
				}
				break;
			case 194793421:
				if(taskName.equals("AzureBlobUpload")) {
					var3 = 7;
				}
				break;
			case 208262586:
				if(taskName.equals("SecondOrderMoment")) {
					var3 = 24;
				}
				break;
			case 299478993:
				if(taskName.equals("ZipMultipleBuffer")) {
					var3 = 11;
				}
				break;
			case 390231307:
				if(taskName.equals("DecisionTreeTrainBatched")) {
					var3 = 13;
				}
				break;
			case 425263264:
				if(taskName.equals("BlockWindowAverage")) {
					var3 = 0;
				}
				break;
			case 464283758:
				if(taskName.equals("DecisionTreeTrain")) {
					var3 = 19;
				}
				break;
			case 605255814:
				if(taskName.equals("NoOperation")) {
					var3 = 27;
				}
				break;
			case 619493628:
				if(taskName.equals("XMLParse")) {
					var3 = 15;
				}
				break;
			case 630122827:
				if(taskName.equals("MQTTPublish")) {
					var3 = 10;
				}
				break;
			case 665979947:
				if(taskName.equals("LineChartPlot")) {
					var3 = 28;
				}
				break;
			case 1099170398:
				if(taskName.equals("DecisionTreeClassify")) {
					var3 = 18;
				}
				break;
			case 1358293380:
				if(taskName.equals("Interpolation")) {
					var3 = 25;
				}
				break;
			case 1496001602:
				if(taskName.equals("LinearRegressionTrain")) {
					var3 = 21;
				}
				break;
			case 1736502163:
				if(taskName.equals("PiByViete")) {
					var3 = 14;
				}
				break;
			case 1759069855:
				if(taskName.equals("AzureTable")) {
					var3 = 8;
				}
				break;
			case 1762353840:
				if(taskName.equals("AzureWrite")) {
					var3 = 9;
				}
				break;
			case 1887393916:
				if(taskName.equals("KalmanFilter")) {
					var3 = 23;
				}
				break;
			case 1988760745:
				if(taskName.equals("Accumlator")) {
					var3 = 2;
				}
		}

		switch(var3) {
			case 0:
				return newBlockWindowAverageBolt(p);
			case 1:
				return newDistinctApproxCountBolt(p);
			case 2:
				return newAccumlatorBolt(p);
			case 3:
				return newBloomFilterCheckBolt(p);
			case 4:
				return newBloomFilterTrainBolt(p);
			case 5:
				return newRangeFilterCheck(p);
			case 6:
				return newAzureBlobDownloadTaskBolt(p);
			case 7:
				return newAzureBlobUploadTaskBolt(p);
			case 8:
				return newAzureTableTaskBolt(p);
			case 9:
				return newAzureTableInsertBolt(p);
			case 10:
				return newMQTTPublishTaskBolt(p);
			case 11:
				return newZipMultipleBuffer(p);
			case 12:
				return newLinearRegressionTrainBatched(p);
			case 13:
				return newDecisionTreeTrainBatched(p);
			case 14:
				return newPiByVieteBolt(p);
			case 15:
				return newXMLParseBolt(p);
			case 16:
				return newSenMLParse(p);
			case 17:
				return newCsvToSenMlParse(p);
			case 18:
				return newDecisionTreeClassifyBolt(p);
			case 19:
				return newDecisionTreeTrainBolt(p);
			case 20:
				return newLinearRegressionPredictorBolt(p);
			case 21:
				return newLinearRegressionTrainBolt(p);
			case 22:
				return newSimpleLinearRegressionPredictorBolt(p);
			case 23:
				return newKalmanFilterBolt(p);
			case 24:
				return newSecondOrderMomentBolt(p);
			case 25:
				return newInterpolationBolt(p);
			case 26:
				return newAnnotateBolt(p);
			case 27:
				return newNoOperationBolt(p);
			case 28:
				return newLineChartPlotBolt(p);
			case 29:
				return newMultiLineChartPlotBolt(p);
			default:
				throw new IllegalArgumentException("Unknown class name for bolt/task: " + taskName);
		}
	}

	public static BaseTaskBolt newBlockWindowAverageBolt(Properties p) {
		return new BlockWindowAverageBolt(p);
	}

	public static BaseTaskBolt newDistinctApproxCountBolt(Properties p) {
		return new DistinctApproxCountBolt(p);
	}

	public static BaseTaskBolt newAccumlatorBolt(Properties p) {
		return new AccumlatorBolt(p);
	}

	public static BaseTaskBolt newBloomFilterCheckBolt(Properties p) {
		return new BloomFilterCheckBolt(p);
	}

	public static BaseTaskBolt newBloomFilterTrainBolt(Properties p) {
		return new BloomFilterTrainBolt(p);
	}

	public static BaseTaskBolt newRangeFilterCheck(Properties p) {
		return new RangeFilterCheckBolt(p);
	}

	public static BaseTaskBolt newAzureBlobDownloadTaskBolt(Properties p) {
		return new AzureBlobDownloadTaskBolt(p);
	}

	public static BaseTaskBolt newAzureBlobUploadTaskBolt(Properties p) {
		return new AzureBlobUploadTaskBolt(p);
	}

	public static BaseTaskBolt newAzureTableTaskBolt(Properties p) {
		return new AzureTableTaskBolt(p);
	}

	public static BaseTaskBolt newMQTTPublishTaskBolt(Properties p) {
		return new MQTTPublishTaskBolt(p);
	}

	public static BaseTaskBolt newAzureTableInsertBolt(Properties p) {
		return new AzureTableInsertBolt(p);
	}

	public static BaseTaskBolt newZipMultipleBuffer(Properties p) {
		return new ZipMultipleBufferBolt(p);
	}

	public static BaseTaskBolt newLinearRegressionTrainBatched(Properties p) {
		return new LinearRegressionTrainBatchedBolt(p);
	}

	public static BaseTaskBolt newDecisionTreeTrainBatched(Properties p) {
		return new DecisionTreeTrainBatchedBolt(p);
	}

	public static BaseTaskBolt newPiByVieteBolt(Properties p) {
		return new PiByVieteBolt(p);
	}

	public static BaseTaskBolt newXMLParseBolt(Properties p) {
		return new XMLParseBolt(p);
	}

	public static BaseTaskBolt newSenMLParse(Properties p) {
		return new SenMlParseBolt(p);
	}

	public static BaseTaskBolt newCsvToSenMlParse(Properties p) {
		return new CsvToSenMlParseBolt(p);
	}

	public static BaseTaskBolt newDecisionTreeClassifyBolt(Properties p) {
		return new DecisionTreeClassifyBolt(p);
	}

	public static BaseTaskBolt newDecisionTreeTrainBolt(Properties p) {
		return new DecisionTreeTrainBolt(p);
	}

	public static BaseTaskBolt newLinearRegressionPredictorBolt(Properties p) {
		return new LinearRegressionPredictorBolt(p);
	}

	public static BaseTaskBolt newLinearRegressionTrainBolt(Properties p) {
		return new LinearRegressionTrainBolt(p);
	}

	public static BaseTaskBolt newSimpleLinearRegressionPredictorBolt(Properties p) {
		return new SimpleLinearRegressionPredictorBolt(p);
	}

	public static BaseTaskBolt newKalmanFilterBolt(Properties p) {
		return new KalmanFilterBolt(p);
	}

	public static BaseTaskBolt newSecondOrderMomentBolt(Properties p) {
		return new SecondOrderMomentBolt(p);
	}

	public static BaseTaskBolt newInterpolationBolt(Properties p) {
		return new InterpolationBolt(p);
	}

	public static BaseTaskBolt newAnnotateBolt(Properties p) {
		return new AnnotateBolt(p);
	}

	public static BaseTaskBolt newNoOperationBolt(Properties p) {
		return new NoOperationBolt(p);
	}

	public static BaseTaskBolt newLineChartPlotBolt(Properties p) {
		return new LineChartPlotBolt(p);
	}

	public static BaseTaskBolt newMultiLineChartPlotBolt(Properties p) {
		return new MultiLineChartPlotBolt(p);
	}
}
*/