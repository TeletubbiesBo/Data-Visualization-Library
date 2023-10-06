package ui;

class AlgortithmConfiguration {
    private int maxiteration=1;
    private int updateinterval=1;
    private int numOfcluster=1;

    public AlgortithmConfiguration(String maxiterationStr, String updateintervalStr) throws NumberFormatException{
        maxiteration = Integer.parseInt(maxiterationStr);
        updateinterval = Integer.parseInt(updateintervalStr);

        if (maxiteration < 0 ) {
            maxiteration =1;
        }
        if(updateinterval < 0)
            updateinterval=1;
    }

    public AlgortithmConfiguration(String maxiterationStr, String updateintervalStr, String numOfclusterStr) throws NumberFormatException{
        maxiteration = Integer.parseInt(maxiterationStr);
        updateinterval = Integer.parseInt(updateintervalStr);
        numOfcluster = Integer.parseInt(numOfclusterStr);

        if (maxiteration < 0 || updateinterval < 0 || numOfcluster < 0) {
            this.maxiteration = 1;
            this.updateinterval = 1;
            this.numOfcluster = 1;
        }
    }

    public int getMaxiteration() {
        return maxiteration;
    }

    public int getUpdateinterval() {
        return updateinterval;
    }

    public int getNumOfcluster() {
        return numOfcluster;
    }

}
