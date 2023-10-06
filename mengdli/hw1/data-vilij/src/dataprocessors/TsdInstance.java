package dataprocessors;
import data.DataSet;
import javafx.geometry.Point2D;

public class TsdInstance {
    private String name;
    private String label;
    private Point2D location;

    public TsdInstance(String name,String label,Point2D location){
        this.name = name;
        this.label = label;
        this.location = location;
    }

    public String getName(){
        return name;
    }

    public String getLabel(){
        return label;
    }

    public Point2D getLocation(){
        return location;
    }


    public static TsdInstance parseTsdString(String tsdLine) throws Exception{
        TsdInstance tsdInstance =null;

        // To create an object:
        // need: name
        //need: lable
        //need: location

        String[] arr = tsdLine.split("\t");
        String name= nameFormatCheck(arr[0]);
        String label = arr[1];
        Point2D point2D = locationOf(arr[2]);

        tsdInstance = new TsdInstance(name, label, point2D);

        return  tsdInstance;
    }

    private static String nameFormatCheck(String name) throws DataSet.InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new DataSet.InvalidDataNameException(name);
        return name;
    }
    private static Point2D locationOf(String locationString) throws  Exception {
        String[] coordinateStrings = locationString.trim().split(",");
        double x = Double.parseDouble(coordinateStrings[0]),
                y = Double.parseDouble(coordinateStrings[1]);

//        if (x< 0)
//            throw  new  Exception("X value out of boundary");
//        if (y< 0)
//            throw  new  Exception("Y value out of boundary");
        return new Point2D(x, y);
    }
}
