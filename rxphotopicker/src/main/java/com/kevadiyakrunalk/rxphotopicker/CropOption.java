package com.kevadiyakrunalk.rxphotopicker;

/**
 * Created by KevadiyaKrunalK on 12-12-2016.
 */

public class CropOption {
    private int outputX;
    private int outputY;
    private int aspectX;
    private int aspectY;
    private boolean scale;

    public int getOutputX() {
        return outputX;
    }

    public void setOutputX(int outputX) {
        this.outputX = outputX;
    }

    public int getOutputY() {
        return outputY;
    }

    public void setOutputY(int outputY) {
        this.outputY = outputY;
    }

    public int getAspectX() {
        return aspectX;
    }

    public void setAspectX(int aspectX) {
        this.aspectX = aspectX;
    }

    public int getAspectY() {
        return aspectY;
    }

    public void setAspectY(int aspectY) {
        this.aspectY = aspectY;
    }

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public static class Builder {
        private CropOption cropOption;

        public Builder() {
            cropOption = new CropOption();
            cropOption.setOutputX(512);
            cropOption.setOutputY(512);
            cropOption.setAspectX(1);
            cropOption.setAspectY(1);
            cropOption.setScale(true);
        }

        public void setOutputHW(int outputx, int outputy) {
            cropOption.setOutputX(outputx);
            cropOption.setOutputY(outputy);
        }

        public void setAspectRatio(int aspectx, int aspecty) {
            cropOption.setAspectX(aspectx);
            cropOption.setAspectY(aspecty);
        }

        public void setScale(boolean scale){
            cropOption.setScale(scale);
        }

        public int getOutputX(){
            return cropOption.getOutputX();
        }

        public int getOutputY(){
            return cropOption.getOutputY();
        }

        public int getAspectX(){
            return cropOption.getAspectX();
        }

        public int getAspectY(){
            return cropOption.getAspectY();
        }

        public boolean isScale(){
            return cropOption.isScale();
        }
    }
}
