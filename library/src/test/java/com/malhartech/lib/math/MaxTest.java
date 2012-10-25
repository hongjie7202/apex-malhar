/**
 * Copyright (c) 2012-2012 Malhar, Inc. All rights reserved.
 */
package com.malhartech.lib.math;

import com.malhartech.api.OperatorConfiguration;
import com.malhartech.api.Sink;
import com.malhartech.dag.Tuple;
import java.util.HashMap;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MaxTest {
    private static Logger LOG = LoggerFactory.getLogger(Range.class);

  class TestSink implements Sink
  {
    HashMap<String, Number> collect = null;
    @Override
    public void process(Object payload)
    {
      if (payload instanceof Tuple) {
      }
      else {
        collect = (HashMap<String, Number>) payload;
      }
    }
  }


    /**
     * Test functional logic
     */
    @Test
    public void testNodeProcessing() {
      testSchemaNodeProcessing(new Min<String,Integer>(), "integer"); // 8million/s
      testSchemaNodeProcessing(new Min<String,Double>(), "double"); // 8 million/s
      testSchemaNodeProcessing(new Min<String,Long>(), "long"); // 8 million/s
      testSchemaNodeProcessing(new Min<String,Short>(),"short"); // 8 million/s
      testSchemaNodeProcessing(new Min<String,Float>(),"float"); // 8 million/s
    }

    /**
     * Test oper logic emits correct results for each schema
     */
    public void testSchemaNodeProcessing(Min oper, String type)
    {
      TestSink minSink = new TestSink();
      oper.min.setSink(minSink);
      oper.setup(new OperatorConfiguration());

      oper.beginWindow();
      
      HashMap<String, Number> input = new HashMap<String, Number>();
      int numtuples = 1000000;
      // For benchmark do -> numtuples = numtuples * 100;
      if (type.equals("integer")) {
        HashMap<String,Integer> tuple;
        for (int i = 0; i < numtuples; i++) {
          tuple = new HashMap<String, Integer>();
          tuple.put("a", new Integer(i));
          oper.data.process(tuple);
        }
      }
      else if (type.equals("double")) {
        HashMap<String,Double> tuple;
        for (int i = 0; i < numtuples; i++) {
          tuple = new HashMap<String, Double>();
          tuple.put("a", new Double(i));
          oper.data.process(tuple);
        }
      }
      else if (type.equals("long")) {
        HashMap<String,Long> tuple;
        for (int i = 0; i < numtuples; i++) {
          tuple = new HashMap<String, Long>();
          tuple.put("a", new Long(i));
          oper.data.process(tuple);
        }
      }
      else if (type.equals("short")) {
        HashMap<String,Short> tuple;
        int count = numtuples/1000; // cannot cross 64K
        for (int j = 0; j < count; j++) {
          for (short i = 0; i < 1000; i++) {
            tuple = new HashMap<String, Short>();
            tuple.put("a", new Short(i));
            oper.data.process(tuple);
          }
        }
      }
       else if (type.equals("float")) {
        HashMap<String,Float> tuple;
        for (int i = 0; i < numtuples; i++) {
          tuple = new HashMap<String, Float>();
          tuple.put("a", new Float(i));
          oper.data.process(tuple);
        }
      }
      oper.endWindow();

      Assert.assertEquals("number emitted tuples", 1, minSink.collect.size());
      Number val = minSink.collect.get("a");
      if (type.equals("short")) {
        Assert.assertEquals("emitted min value was ", new Double(numtuples/1000), val);
      }
      else {
        Assert.assertEquals("emitted min value was ", new Double(numtuples), val);
      }
    }
}
