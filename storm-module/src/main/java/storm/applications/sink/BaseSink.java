package storm.applications.sink;

import backtype.storm.tuple.Fields;
import org.slf4j.Logger;
import storm.applications.bolt.AbstractBolt;
import storm.applications.constants.BaseConstants.BaseConf;
import storm.applications.sink.formatter.BasicFormatter;
import storm.applications.sink.formatter.Formatter;
import storm.applications.util.config.ClassLoaderUtils;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public abstract class BaseSink extends AbstractBolt {
    protected Formatter formatter;
    
    @Override
    public void initialize() {


        String sinkClass = config.getString(getConfigKey(BaseConf.SINK_CLASS), null);
        // chevk if its a FileSink and create a file accordingly

        // for file sink
        String taskId      = String.valueOf(context.getThisTaskId());
       /* File file = new File("/output/result_"+taskId+".dat");
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

       String str = "/output/result_"+taskId+".dat";
        Path path = Paths.get(str);
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            System.err.println("already exists: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }



        String formatterClass = config.getString(getConfigKey(BaseConf.SINK_FORMATTER), null);
        
        if (formatterClass == null) {
            formatter = new BasicFormatter();
        } else {
            formatter = (Formatter) ClassLoaderUtils.newInstance(formatterClass, "formatter", getLogger());
        }
        
        formatter.initialize(config, context);
    }

    @Override
    public Fields getDefaultFields() {
        return new Fields("");
    }
    
    protected String getConfigKey(String template) {
        return String.format(template, configPrefix);
    }
    
    protected abstract Logger getLogger();
}
