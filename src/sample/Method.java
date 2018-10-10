package sample;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

public class Method implements SeachTime{
    CountDownLatch latch;
    String function;
    BigDecimal x0;
    BigDecimal x1;
    BigDecimal h0;
    BigDecimal h1;
    BigDecimal r;
    BigDecimal tol;
    BigDecimal functionx0;
    BigDecimal functionx1;
    String perem;
    long iter = 0;
    int iteration;
    int itermax;
    long timeMax;
    long startTime;
    long timeLimit;
    long resultTime;
    long pauseTimeStart;

    @Override
    public void StartPause() {
        pauseTimeStart = System.currentTimeMillis();
    }

    @Override
    public void StopPause() {
        startTime = startTime + (System.currentTimeMillis() - pauseTimeStart);
    }

    public void SearchMin(){
    if(functionx1.compareTo(functionx0)>=0){

        if(h0.abs().compareTo(tol.divide(r)) ==-1){
            h1=h0;
            x1=x0;
        }
        else{
            h1= new BigDecimal(-1).multiply(h0).divide(r);
            h0=h1;
            x1=x0.add(h1);
        }
    }
    else {
        x0=x1;
        h1=h0;
        x1=x0.add(h1);
    }
    }
    public void SearchMax(){
        if(functionx1.compareTo(functionx0)<=0){
            if(h1.abs().compareTo(tol.divide(r))==-1){
                h1=h0;
                x1=x0;
            }
            else{
                h1= new BigDecimal(- 1).multiply((h0).divide(r));
                h0=h1;
                x1=x0.add(h1);
            }
        }
        else {
            x0=x1;
            h1=h0;
            x1=x0.add(h1);
        }
    }
}
