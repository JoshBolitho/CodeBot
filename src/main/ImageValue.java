package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ImageValue implements Value {

    BufferedImage image;

    //Create an ImageValue from dimensions
    public ImageValue(int x, int y){
        if(x>ScriptExecutor.getMaxImageSize() || x<0 || y>ScriptExecutor.getMaxImageSize() || y<0) {
            throw new ScriptException("Unable to create image with dimensions: "+x+","+y);
        }
        this.image = new BufferedImage(x,y,BufferedImage.TYPE_INT_RGB);
    }

    //Create and ImageValue from existing BufferedImage
    //Will be inaccessible to BotScript users, and will be used to import custom images
    public ImageValue(BufferedImage image) {
        if(image == null){
            System.out.println("Warning: null image parameter, initialising image as blank 100x100 instead.");
            this.image = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB); }
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
        if(x<image.getWidth() && y< image.getHeight() && x>=0 && y >=0) {
            image.setRGB(x, y, colour.getRGB());
        }else{
            throw new ScriptException(String.format("Coordinates (%s,%s) don't fit within image dimensions (%s,%s)",x,y,image.getWidth(),image.getHeight()));
        }
    }

    public ArrayValue getPixel(int x, int y){
        //Ensure x,y are in bounds
        if(x<image.getWidth() && y< image.getHeight() && x>=0 && y >=0) {
            Color myColour = new Color(image.getRGB(x,y));

            ArrayList<Expression> elements = new ArrayList<>();
            elements.add(new ValueExpression(new IntegerValue(myColour.getRed())));
            elements.add(new ValueExpression(new IntegerValue(myColour.getGreen())));
            elements.add(new ValueExpression(new IntegerValue(myColour.getBlue())));

            return new ArrayValue(elements);

        }else{
            throw new ScriptException(String.format("Coordinates (%s,%s) don't fit within image dimensions (%s,%s)",x,y,image.getWidth(),image.getHeight()));
        }


    }

    public Integer getWidth(){
        return image.getWidth();
    }
    public Integer getHeight(){
        return image.getHeight();
    }

    @Override
    public String toString() {
        return "Image";
    }

    @Override
    public ValueType getType() {
        return ValueType.IMAGE;
    }

    @Override
    public boolean isType(ValueType v) {
        return v == ValueType.IMAGE;
    }

    @Override
    public String castString() throws ScriptException{
        return this.toString();
    }

    @Override
    public Integer castInteger() throws ScriptException{
        throw new ScriptException(String.format("Failed to cast image to integer"));
    }

    @Override
    public Float castFloat() throws ScriptException{
        throw new ScriptException(String.format("Failed to cast image to float"));
    }

    @Override
    public Boolean castBoolean() throws ScriptException{
        throw new ScriptException(String.format("Failed to cast image to boolean"));
    }

    @Override
    public ArrayList<Value> castArray() throws ScriptException {
        throw new ScriptException(String.format("Failed to cast image to array"));
    }

    @Override
    public BufferedImage castImage() throws ScriptException {
        return image;
    }
}
