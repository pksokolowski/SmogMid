package com.github.pksokolowski.smogmid;

import com.github.pksokolowski.smogmid.utils.DistanceHelper;
import com.github.pksokolowski.smogmid.utils.LatLng;
import org.junit.Test;

import static org.junit.Assert.fail;

public class DistanceHelperTest {

    @Test
    public void isDistanceReturnedCorrect(){
        final var A = new LatLng(52.219298, 21.004724);
        final var B = new LatLng(52.280939, 20.962156);

        final var result = DistanceHelper.distance(A, B);

        if(!areApproximatelySame(result, 7.444, 0.5) ) fail();
    }

    private boolean areApproximatelySame(double a, double b, double tolerance){
        final var difference = Math.abs(a - b);
        return difference <= tolerance;
    }
}
