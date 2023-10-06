package ui;

import algorithms.Algorithm;
import org.junit.Test;

import static org.junit.Assert.*;


public class AlgortithmConfigurationTest {

    /**
     * Test: there is at least one test per algorithm with invalid configuration value(s).
     * <p>
     * Negative input will be changed to the default parameters, which is 1.
     */
    @Test
    public void testclassifier_parameter_is_positive() throws Exception {
        String maxiteration = "-3";
        String updateinterval = "100";
        AlgortithmConfiguration randomClassifer = new AlgortithmConfiguration(maxiteration, updateinterval);
        assert (randomClassifer.getMaxiteration() > 0 && randomClassifer.getUpdateinterval() > 0);
    }

    /**
     * test if the parameters are not integers, it should throw an Exception.
     */
    @Test(expected = NumberFormatException.class)
    public void testclassifier_parameter_not_integer() throws Exception {
        String maxiteration = "abc";
        String updateinterval = "100";
        AlgortithmConfiguration randomClassifer = new AlgortithmConfiguration(maxiteration, updateinterval);
        assert (randomClassifer.getMaxiteration() > 0 && randomClassifer.getUpdateinterval() > 0);
    }


    /**
     * Test: there is at least one test per algorithm with invalid configuration value(s).
     * <p>
     * Negative input will be changed to the default parameters, which is 1.
     */
    @Test
    public void testrandomClusterer_parameter_is_positive() throws Exception {
        String maxiteration = "-3";
        String updateinterval = "100";
        String numOfcluster = "1";
        AlgortithmConfiguration randomCluster = new AlgortithmConfiguration(maxiteration, updateinterval, numOfcluster);
        assert (randomCluster.getMaxiteration() > 0 && randomCluster.getUpdateinterval() > 0 && randomCluster.getNumOfcluster() > 0);
    }

    /**
     * test if the parameters are not integers, it should throw an Exception.
     */
    @Test(expected = NumberFormatException.class)
    public void testrandomClusterer_parameter_not_integer() throws Exception {
        String maxiteration = "abc";
        String updateinterval = "100";
        String numOfcluster = "1";
        AlgortithmConfiguration randomCluster = new AlgortithmConfiguration(maxiteration, updateinterval, numOfcluster);
        assert (randomCluster.getMaxiteration() > 0 && randomCluster.getUpdateinterval() > 0 && randomCluster.getNumOfcluster() > 0);
    }

    /**
     * Test: there is at least one test per algorithm with invalid configuration value(s).
     * <p>
     * Negative input will be changed to the default parameters, which is 1.
     */
    @Test
    public void testkmeanClusterer_parameter_is_positive() {
        String maxiteration = "-3";
        String updateinterval = "100";
        String numOfcluster = "1";
        AlgortithmConfiguration kmeanCluster = new AlgortithmConfiguration(maxiteration, updateinterval, numOfcluster);
        assert (kmeanCluster.getMaxiteration() > 0 && kmeanCluster.getUpdateinterval() > 0 && kmeanCluster.getNumOfcluster() > 0);
    }

    /**
     * test if the parameters are not integers, it should throw an Exception.
     */
    @Test(expected = NumberFormatException.class)
    public void testkmeanClusterer_parameter_not_integer() {
        String maxiteration = "abc";
        String updateinterval = "100";
        String numOfcluster = "1";
        AlgortithmConfiguration kmeanCluster = new AlgortithmConfiguration(maxiteration, updateinterval, numOfcluster);
        assert (kmeanCluster.getMaxiteration() > 0 && kmeanCluster.getUpdateinterval() > 0 && kmeanCluster.getNumOfcluster() > 0);
    }
}