package competition.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.util.*;
import ro.nextreports.engine.chart.*;
import competition.service.ChartService;
import competition.web.util.ConnectionUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChartServiceImpl implements ChartService {
	
	@Transactional(readOnly = true)
	public String getJsonData(String chartFile, Map<String, Object> parameterValues ) {
		InputStream is = getClass().getResourceAsStream("/" + chartFile + ".chart");
		Chart chart = NextChartUtil.loadChart(is);
		
		Connection  connection = ConnectionUtil.createConnection();  	
				
		final ChartRunner runner = new ChartRunner();
        runner.setParameterValues(parameterValues);
        runner.setChart(chart);     
        runner.setGraphicType(ChartRunner.HTML5_TYPE);
        runner.setConnection(connection);        
        
        FutureTask<String> runTask = null;
        try {
			runTask = new FutureTask<String>(new Callable<String>() {
				public String call() throws Exception {
					ByteArrayOutputStream outputStream = null;
					try {
						outputStream = new ByteArrayOutputStream();
						runner.run(outputStream);
						outputStream.close();
						return new String(outputStream.toByteArray());
					} finally {
						if (outputStream != null) {
							try {
								outputStream.close();
							} catch (IOException e) {
								// ignore
							}
						}
					}
				}
			});
        	new Thread(runTask).start();
        	String jsonData = runTask.get(100, TimeUnit.SECONDS);        	
        	return jsonData;
		} catch (Exception e) {			
			e.printStackTrace();
			return "{}";
		} finally {
			ConnectionUtil.closeConnection(connection);
		}		
	}

}
