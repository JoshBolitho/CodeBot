import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageVariable implements Variable{

    BufferedImage image;

    //Create an ImageVariable from dimensions
    public ImageVariable(int x, int y) {
        this.image = new BufferedImage(x,y,BufferedImage.TYPE_INT_RGB);
    }

    //Create and ImageVariable from existing Bufferedimage
    //Will be inaccessible to BotScript users, and will be used to import custom images
    public ImageVariable(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }


    public void setPixel(int x, int y, Color colour){
        //Ensure x,y are in bounds
        if(x<image.getWidth() && y< image.getHeight()) {
            image.setRGB(x, y, colour.getRGB());
        }else{
            throw new ExecutionException(String.format("Coordinates (%s,%s) don't fit within image dimensions (%s,%s)",x,y,image.getWidth(),image.getHeight()));
        }
    }

    public ArrayVariable getPixel(int x, int y){
        //Ensure x,y are in bounds
        if(x<image.getWidth() && y< image.getHeight()) {
            Color myColour = new Color(image.getRGB(x,y));

            ArrayList<Expression> elements = new ArrayList<>();
            elements.add(new Expression(new IntegerVariable(myColour.getRed())));
            elements.add(new Expression(new IntegerVariable(myColour.getGreen())));
            elements.add(new Expression(new IntegerVariable(myColour.getBlue())));

            return new ArrayVariable(elements);

        }else{
            throw new ExecutionException(String.format("Coordinates (%s,%s) don't fit within image dimensions (%s,%s)",x,y,image.getWidth(),image.getHeight()));
        }


    }


    @Override
    public String toString() {
        return "Image";
    }

    @Override
    public Object getValue() {
        return image;
    }

    @Override
    public VariableType getType() {
        return VariableType.IMAGE;
    }

    @Override
    public String castString() throws ExecutionException{
        return this.toString();
    }

    @Override
    public Integer castInteger() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast image to integer"));
    }

    @Override
    public Float castFloat() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast image to float"));
    }

    @Override
    public Boolean castBoolean() throws ExecutionException{
        throw new ExecutionException(String.format("Failed to cast image to boolean"));
    }

    @Override
    public ArrayList<Variable> castArray() throws ExecutionException {
        throw new ExecutionException(String.format("Failed to cast image to array"));
    }

    @Override
    public BufferedImage castImage() throws ExecutionException {
        return image;
    }
}
