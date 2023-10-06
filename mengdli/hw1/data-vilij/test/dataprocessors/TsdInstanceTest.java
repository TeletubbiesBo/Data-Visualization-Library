package dataprocessors;

import data.DataSet;
import org.junit.Test;

import static org.junit.Assert.*;

public class TsdInstanceTest {
//
//    @Test
//    public void parseTsdString() {
//    }


    /**
     * Test Throw format exception
     * */
    @Test (expected = Exception.class)
    public void testparseTsdString_Expect_Exception() throws Exception{
        String line="";
        line="@Instance1\tlabel1";
        TsdInstance Instance = null;

        Instance = TsdInstance.parseTsdString(line);
    }

    /**
     * Test X value is positive*/
    @Test
    public void testparseTsdString_X_is_positive() throws Exception{
        String line="";
        line="@Instance1\tlabel1\t12,12";
        TsdInstance instance = null;
try{
        instance = TsdInstance.parseTsdString(line);}
        catch (Exception e){}

        assert (instance.getLocation().getX() > 0);
    }

    /**
     * Test Y value is positive*/
    @Test
    public void testparseTsdString_Y_is_positive() {
        String line="";
        line="@Instance1\tlabel1\t12,12";
        TsdInstance instance = null;
        try{
        instance = TsdInstance.parseTsdString(line);}
        catch (Exception e) {}
        assert (instance.getLocation().getY() > 0);
    }
    @Test
    public void parseTsdString() {
    }
}