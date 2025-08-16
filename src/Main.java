
import java.io.*;
import java.util.TreeMap;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class test extends Application {
    short cthead[][][]; //store the 3D volume data set
    float grey[][][]; //store the 3D volume data set converted to 0-1 ready to copy to the image
    short min, max; //min/max value in the 3D volume data set

    //Starting Slices
    int currZSlice = 128;
    int currYSlice = 128;
    int currXSlice = 128;


    double currentSkinOpacity = 0.12;
    int skinOpacityInt;

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        stage.setTitle("CThead Viewer");

        try {
            ReadData();
        } catch (IOException e) {
            System.out.println("Error: The CThead file is not in the working directory");
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            return;
        }

        //We need 3 things to see an image
        //1. We need to create the image
        WritableImage sliceZImage = new WritableImage(256, 256); //allocate memory for the image
        GetZSlice(currZSlice, sliceZImage); //make the image - in this case go get the slice and copy it into the image
        //2. We link a view in the GUI to that image
        ImageView sliceZView = new ImageView(sliceZImage); //and then see 3. below
        // Do the same for MIP
        WritableImage MIPZImage = new WritableImage(256, 256);
        GetZMIP(MIPZImage);
        ImageView MIPZView = new ImageView(MIPZImage);

        //Create the simple GUI
        Slider sliceZSlider = new Slider(0, 255, currZSlice);


        sliceZSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue <? extends Number >
                                        observable, Number oldValue, Number newValue) {

                currZSlice = newValue.intValue();
                //System.out.println(currZSlice);
                //We update our Image
                GetZSlice(currZSlice, sliceZImage); //go get the slice image
                //Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
            }
        });

        //Add all the GUI elements
        WritableImage yView = new WritableImage(256,256);
        GetYSlice(currYSlice, yView);
        ImageView sliceYView = new ImageView(yView);

        WritableImage mipX = new WritableImage(256,256);
        GetXMIP(mipX);
        ImageView mipXView = new ImageView(mipX);

        Slider sliceYSlider = new Slider(0,255,currYSlice);
        sliceYSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue <? extends Number >
                                        observable, Number oldValue, Number newValue) {

                currYSlice = newValue.intValue();
                //System.out.println(currZSlice);
                //We update our Image
                GetYSlice(currYSlice,yView); //go get the slice image
                //Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
            }
        });

        WritableImage xView = new WritableImage(256,256);
        GetXSlice(currXSlice,xView);
        ImageView sliceXView = new ImageView(xView);

        Slider sliceXSlider = new Slider(0,255,currXSlice);
        sliceXSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue <? extends Number >
                                        observable, Number oldValue, Number newValue) {

                currXSlice = newValue.intValue();
                //System.out.println(currZSlice);
                //We update our Image
                GetXSlice(currXSlice,xView); //go get the slice image
                //Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
            }
        });

        WritableImage mipY =  new WritableImage(256,256);
        GetYMIP(mipY);
        ImageView mipYView = new ImageView(mipY);


        WritableImage volumeRenderingZ = new WritableImage(256,256);
        getZVolumeRendering(volumeRenderingZ,currentSkinOpacity);
        ImageView volumeRenderingZView = new ImageView(volumeRenderingZ);

        WritableImage volumeRenderingX= new WritableImage(256,256);
        getXVolumeRendering(volumeRenderingX,currentSkinOpacity);
        ImageView volumeRenderingXView = new ImageView(volumeRenderingX);


        WritableImage volumeRenderingY= new WritableImage(256,256);
        getYVolumeRendering(volumeRenderingY,currentSkinOpacity);
        ImageView volumeRenderingYView = new ImageView(volumeRenderingY);





        Slider opacitySliderVolumeRendering = new Slider(0,100,skinOpacityInt);
        opacitySliderVolumeRendering.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue <? extends Number >
                                        observable, Number oldValue, Number newValue) {
                //Normalising skinOpacity from an int between 0 and 100 to a double between 1 and 0 so it conforms to an opacity
                skinOpacityInt = newValue.intValue();
                currentSkinOpacity = (double) skinOpacityInt / 100;
                System.out.println(currentSkinOpacity + " SKIN OPACITY");

                //We update our Image
                getZVolumeRendering(volumeRenderingZ,currentSkinOpacity);
                getYVolumeRendering(volumeRenderingY,currentSkinOpacity);
                getXVolumeRendering(volumeRenderingX,currentSkinOpacity);
                //go get the slice image
                //Because sliceZView (an ImageView) is linked to it, this will automatically update the displayed image in the GUI
            }
        });

        //I'll start a grid for you
        GridPane grid = new GridPane();
        grid.add(sliceZSlider, 2, 0); // Slider at column 0, row 0
        grid.add(sliceYSlider,1,0);
        grid.add(sliceXSlider,0,0);
        grid.setHgap(10);
        grid.setVgap(10);

        //3. (referring to the 3 things we need to display an image)
        //we need to add it to the grid
        //left value is row right is column
        grid.add(sliceZView, 2, 1); // Slider at column 0, row 1
        grid.add(sliceYView,1,1);
        grid.add(sliceXView,0,1);

        grid.add(MIPZView, 2, 2); // Slider at column 0, row 1
        grid.add(mipYView,1,2);
        grid.add(mipXView,0,2);

        //vol rendering imageviews
        grid.add(volumeRenderingZView,2,3);
        grid.add(volumeRenderingYView,1,3);
        grid.add(volumeRenderingXView,0,3);

        //sliders
        grid.add(opacitySliderVolumeRendering,1,4);




        // Create a scene and set the stage
        Scene scene = new Scene(grid, 800, 840);
        stage.setTitle("CT Data Viewer");
        stage.setScene(scene);
        stage.show();
    }


    //Function to read in the cthead data set
    public void ReadData() throws IOException {
        //If you've put the test.java in a directory called "src" and put the dataset in the parent directory, then this will be the correct path
        File file = new File("CThead-256cubed.bin");
        //Read the data quickly via a buffer (in C++ you can just do a single fread - I couldn't find the equivalent in Java)
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        int i, j, k; //loop through the 3D data set

        min=Short.MAX_VALUE; max=Short.MIN_VALUE; //set to extreme values
        short read; //value read in
        int b1, b2; //data is wrong Endian (check wikipedia) for Java so we need to swap the bytes around

        cthead = new short[256][256][256]; //allocate the memory - note this is fixed for this data set
        grey= new float[256][256][256];
        //loop through the data reading it in
        for (k=0; k<256; k++) {
            for (j=0; j<256; j++) {
                for (i=0; i<256; i++) {
                    //because the Endianess is wrong, it needs to be read byte at a time and swapped
                    b1=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types (C++ is so much easier!)
                    b2=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types (C++ is so much easier!)
                    read=(short)((b2<<8) | b1); //and swizzle the bytes around
                    if (read<min) min=read; //update the minimum
                    if (read>max) max=read; //update the maximum
                    cthead[k][j][i]=read; //put the short into memory (in C++ you can replace all this code with one fread)
                }
            }
        }
        System.out.println(min+" "+max); //diagnostic - for CThead-256cubed.bin this should be -1897, 3029
        //(i.e. there are 4927 levels of grey, and now we will normalise them to 0-1 for display purposes
        //I know the min and max already, so I could have put the normalisation in the above loop, but I put it separate here
        for (k=0; k<256; k++) {
            for (j=0; j<256; j++) {
                for (i=0; i<256; i++) {
                    grey[k][j][i]=((float) cthead[k][j][i]-(float) min)/((float) max-(float) min);
                }
            }
        }
        //At this point, cthead is the original dataset
        //and grey is 0-1 float data that can be displayed by Java
    }

    public void GetXMIP(WritableImage image) {
        //Find the width and height of the image to be process
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        //Get an interface to write to that image memory
        PixelWriter image_writer = image.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //Implement MIP here
                double maxSoFar = min;
                //But I'll just make a white colour and copy it into the image
                for (int i = 0; i < 256; i++) {
                    maxSoFar = Math.max(cthead[y][x][i],maxSoFar);
                }
                System.out.println(maxSoFar + " MAX SO FAR");
                double normalised = (maxSoFar - min)/(max - min);

                System.out.println(normalised + " NORM");
                Color color=Color.color(normalised, normalised, normalised);

                //Apply the new colour
                image_writer.setColor(x, y, color);
            }
        }
    }

    public void GetYMIP(WritableImage image) {
        //Find the width and height of the image to be process
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        //Get an interface to write to that image memory
        PixelWriter image_writer = image.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //Implement MIP here
                double maxSoFar = min;
                //But I'll just make a white colour and copy it into the image
                for (int i = 0; i < 256; i++) {
                    //get max density value of ray then normalise it to get the colour
                    maxSoFar = Math.max(cthead[y][i][x],maxSoFar);
                }

                double normalised = (maxSoFar - min)/(max - min);

                Color color=Color.color(normalised, normalised, normalised);

                //Apply the new colour
                image_writer.setColor(x, y, color);
            }
        }
    }

    public void GetZMIP(WritableImage image) {
        //Find the width and height of the image to be process
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        //Get an interface to write to that image memory
        PixelWriter image_writer = image.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //Implement MIP here
                double maxSoFar = min;
                //But I'll just make a white colour and copy it into the image
                for (int i = 0; i < 256; i++) {
                    //get max density value of ray then normalise it to get the colour
                    maxSoFar = Math.max(cthead[i][y][x],maxSoFar);
                }
                System.out.println(maxSoFar + " MAX SO FAR");
                double normalised = (maxSoFar - min)/(max - min);

                System.out.println(normalised + " NORM");
                Color color=Color.color(normalised, normalised, normalised);

                //Apply the new colour
                image_writer.setColor(x, y, color);
            }
        }
    }

    public void GetZSlice(int slice, WritableImage image) {
        //Find the width and height of the image to be process
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        float val;

        //Get an interface to write to that image memory
        PixelWriter image_writer = image.getPixelWriter();

        //Iterate over all pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //I'm going to get the middle slice as an example
                val = grey[currZSlice][y][x];

                //Or uncomment this to make a grey image dependent on the slider value so you can see how the GUI updates
                //val = (float) slice / 255.f;

                Color color = Color.color(val, val, val);
                //Apply the new colour
                image_writer.setColor(x, y, color);
            }
        }
    }
    public void GetYSlice(int slice, WritableImage image) {
        //Find the width and height of the image to be process
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();
        float val;

        //Get an interface to write to that image memory
        PixelWriter image_writer = image.getPixelWriter();

        //Iterate over all pixels
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                //I'm going to get the middle slice as an example
                val = grey[z][currYSlice][x];

                //Or uncomment this to make a grey image dependent on the slider value so you can see how the GUI updates
                //val = (float) slice / 255.f;

                Color color = Color.color(val, val, val);
                //Apply the new colour
                image_writer.setColor(x, z, color);
            }
        }
    }
    public void GetXSlice(int slice, WritableImage image) {
        //Find the width and height of the image to be process
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        float val;

        //Get an interface to write to that image memory
        PixelWriter image_writer = image.getPixelWriter();

        //Iterate over all pixels
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < width; z++) {
                //I'm going to get the middle slice as an example
                val = grey[y][z][currXSlice];

                //Or uncomment this to make a grey image dependent on the slider value so you can see how the GUI updates
                //val = (float) slice / 255.f;

                Color color = Color.color(val, val, val);
                //Apply the new colour
                image_writer.setColor(z, y, color);
            }
        }
    }

    public void getZVolumeRendering(WritableImage image,double skinOpacity){
        //Uses back to front composition
        PixelWriter image_writer = image.getPixelWriter();
        int width = (int) image.getWidth();
        int height = (int)image.getHeight();
        TreeMap <Short,Double[]> hounseFieldValLookupTable=  new TreeMap<>();
        hounseFieldValLookupTable.put((short) -1897.00,new Double[]{0.0,0.0,0.0,0.0});
        hounseFieldValLookupTable.put((short) -300.00, new Double[]{0.82,0.49,0.18,skinOpacity});
        hounseFieldValLookupTable.put((short) 50.00,new Double[]{0.0,0.0,0.0,0.0});
        hounseFieldValLookupTable.put((short) 300.00,new Double[]{1.0,1.0,1.0,0.8});

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //resets values after every ray
                double redAcc = 0;
                double blueAcc = 0;
                double greenAcc = 0;
                double light = 1;
                double opacity = 0;
                double red = 0;
                double green = 0;
                double blue = 0;
                double opacityAcc = 1;
                //casting the ray
                for (int z = 255; z >= 0; z--) {
                    short hounsfieldValue = cthead[z][y][x];
                    //Used Lookup table to generate RGB and Opacity values for efficiency
                    Double[] colourArray = hounseFieldValLookupTable.floorEntry(hounsfieldValue).getValue();
                    red = colourArray[0];
                    green = colourArray[1];
                    blue = colourArray[2];
                    opacity = colourArray[3];
                    //Works out colours
                    redAcc = opacity  * light * red + (1 - opacity) * redAcc;
                    greenAcc = opacity  * light * green + (1 - opacity) * greenAcc;
                    blueAcc = opacity  * light * blue + (1 - opacity) * blueAcc;

                    //Corrects java rounding errors making sure values are between 0 and 1
                    if (redAcc > 1){
                        redAcc = 1;
                    }

                    if (blueAcc > 1) {
                        blueAcc = 1;
                    }

                    if (greenAcc > 1){
                        greenAcc = 1;
                    }

                    if (opacityAcc > 1){
                        opacityAcc = 1;
                    }



                }

                Color color  = Color.color(redAcc,greenAcc,blueAcc,opacityAcc);
                image_writer.setColor(x,y,color);


            }


        }
    }


    public void getXVolumeRendering(WritableImage image,double skinOpacity) {
        //Uses back to front composition
        PixelWriter image_writer = image.getPixelWriter();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        TreeMap<Short, Double[]> hounseFieldValLookupTable = new TreeMap<>();
        hounseFieldValLookupTable.put((short) -1897.00, new Double[]{0.0, 0.0, 0.0, 0.0});
        hounseFieldValLookupTable.put((short) -300.00, new Double[]{0.82, 0.49, 0.18, skinOpacity});
        hounseFieldValLookupTable.put((short) 50.00, new Double[]{0.0, 0.0, 0.0, 0.0});
        hounseFieldValLookupTable.put((short) 300.00, new Double[]{1.0, 1.0, 1.0, 0.8});


        for (int y = 0; y < height; y++) {
            for (int z = 0; z < width; z++) {
                //resets values after every ray
                double redAcc = 0;
                double blueAcc = 0;
                double greenAcc = 0;
                double light = 1;
                double opacity = 1;
                double red = 0;
                double green = 0;
                double blue = 0;

                double opacityAcc = 1;
                //casting the ray
                for (int x = 0; x < 256 ; x++) {
                    short hounsfieldValue = cthead[z][y][x];
                    //Used Lookup table to generate RGB and Opacity values for efficiency
                    Double[] colourArray = hounseFieldValLookupTable.floorEntry(hounsfieldValue).getValue();
                    red = colourArray[0];
                    green = colourArray[1];
                    blue = colourArray[2];
                    opacity = colourArray[3];
                    //Works out colours
                    redAcc = opacity  * light * red + (1 - opacity) * redAcc;
                    greenAcc = opacity  * light * green + (1 - opacity) * greenAcc;
                    blueAcc = opacity  * light * blue + (1 - opacity) * blueAcc;
                    //Corrects java rounding errors making sure values are between 0 and 1
                    if (redAcc > 1) {
                        redAcc = 1;
                    }

                    if (blueAcc > 1) {
                        blueAcc = 1;
                    }

                    if (greenAcc > 1) {
                        greenAcc = 1;
                    }

                    if (opacityAcc > 1) {
                        opacityAcc = 1;
                    }


                }
                //Sets colour of pixel to the accumulated colour
                Color color = Color.color(redAcc, greenAcc, blueAcc, opacityAcc);
                image_writer.setColor(y, z, color);


            }


        }
    }

    public void getYVolumeRendering(WritableImage image,double skinOpacity){
        //Back to front composition
        PixelWriter image_writer = image.getPixelWriter();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        TreeMap<Short, Double[]> hounseFieldValLookupTable = new TreeMap<>();
        //Add values to lookup table
        hounseFieldValLookupTable.put((short) -1897.00, new Double[]{0.0, 0.0, 0.0, 0.0});
        hounseFieldValLookupTable.put((short) -300.00, new Double[]{0.82, 0.49, 0.18, skinOpacity});
        hounseFieldValLookupTable.put((short) 50.00, new Double[]{0.0, 0.0, 0.0, 0.0});
        hounseFieldValLookupTable.put((short) 300.00, new Double[]{1.0, 1.0, 1.0, 0.8});


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                //resets values after every ray
                double redAcc = 0;
                double blueAcc = 0;
                double greenAcc = 0;
                double light = 1;
                double opacity = 1;
                double red = 0;
                double green = 0;
                double blue = 0;

                double opacityAcc = 1;
                //casting the ray
                for (int z = 255; z >= 0; z--) {
                    short hounsfieldValue = cthead[y][z][x];
                    //Used Lookup table to generate RGB and Opacity values for efficiency
                    Double[] colourArray = hounseFieldValLookupTable.floorEntry(hounsfieldValue).getValue();
                    red = colourArray[0];
                    green = colourArray[1];
                    blue = colourArray[2];
                    opacity = colourArray[3];
                    //Works out colours
                    redAcc = opacity  * light * red + (1 - opacity) * redAcc;
                    greenAcc = opacity  * light * green + (1 - opacity) * greenAcc;
                    blueAcc = opacity  * light * blue + (1 - opacity) * blueAcc;

                }
                //Sets colour of pixel to the accumulated colour
                Color color = Color.color(redAcc, greenAcc, blueAcc,opacityAcc);
                image_writer.setColor(x, y, color);

                //To get rid of rounding errors
                if (redAcc > 1) {
                    redAcc = 1;
                }

                if (blueAcc > 1) {
                    blueAcc = 1;
                }

                if (greenAcc > 1) {
                    greenAcc = 1;
                }

                if (opacityAcc > 1) {
                    opacityAcc = 1;
                }
            }


        }
    }


    public static void main (String[]args){
        launch();
    }
}

