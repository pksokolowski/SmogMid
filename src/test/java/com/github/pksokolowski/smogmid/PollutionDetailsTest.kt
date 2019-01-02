package com.github.pksokolowski.smogmid

import com.github.pksokolowski.smogmid.db.PollutionDetails
import org.junit.Assert
import org.junit.Test
import java.lang.Exception
import java.util.*

class PollutionDetailsTest {

    @Test
    fun encodesAndDecodesBackToTheSameThing() {
        val rand = Random(431)
        for (iteration in 0 until 100000) {
            val data = Array(7) { rand.nextInt(7) - 1 }

            val details = getPollutionDetailsFromArray(data)
            val retrievedData = details.getDetailsArray()
            Assert.assertArrayEquals("getting data as an array failed", data, retrievedData)

            val encodedAsInt = details.encode()
            val detailsFromEncodedInt = PollutionDetails(encodedAsInt)
            val retrievedInt = detailsFromEncodedInt.encode()
            Assert.assertEquals("encoding as int failed", encodedAsInt, retrievedInt)

            val detailsArrayFromInitialEncodingAsInt = detailsFromEncodedInt.getDetailsArray()
            Assert.assertArrayEquals("array back from decoded int failed", data, detailsArrayFromInitialEncodingAsInt)
        }
    }

    @Test
    fun encodesAndDecodesProperlyWhenEmpty() {
        val details = PollutionDetails(-1, -1, -1, -1, -1, -1, -1)
        Assert.assertEquals("encoding -1s as 9s failed", 9999999, details.encode())
    }

    @Test
    fun encodesAndDecodesProperlyWithAllValuesMaxedOut() {
        val details = PollutionDetails(5, 5, 5, 5, 5, 5, 5)
        Assert.assertEquals("encoding failed with all pollutants maxed out", 5555555, details.encode())
    }

    @Test
    fun encodesAndDecodesProperlyWithAllValuesSetToZero() {
        val details = PollutionDetails(0, 0, 0, 0, 0, 0, 0)
        val encoded = details.encode()
        Assert.assertEquals("encoding failed with all AQ index values set to zero", 0, encoded)

        val recreatedFromTheEncodedForm = PollutionDetails(encoded)
        val decodedAgain = recreatedFromTheEncodedForm.encode()
        Assert.assertEquals(0, decodedAgain)

        val detailsArray = recreatedFromTheEncodedForm.getDetailsArray()
        Assert.assertArrayEquals(arrayOf(0, 0, 0, 0, 0, 0, 0), detailsArray)
    }

    @Test
    fun throwsExceptionOnFaultyInput() {
        try {
            // pm25 is 10, which is above max allowed value
            PollutionDetails(0, 10, 0, 0, 0, 0, 0)
            Assert.fail()
        } catch (e: Exception) {
        }

        try {
            // so2 = 6 which is out of the official airQualityIndex's range
            PollutionDetails(0, 0, 0, 0, 6, 0, 0)
            Assert.fail()
        } catch (e: Exception) {
        }
    }

    @Test
    fun combinationOfInstancesGivesAThirdInstanceWithAllSubIndexes() {
        // each digit in encoded form is a single subIndex which assumes values 0-5 inclusive
        // remember that 9 stands for -1 index level
        val A = PollutionDetails(1999991)
        val B = PollutionDetails(2991999)
        val C = A.combinedWith(B)

        Assert.assertEquals(1991991, C.encode())
    }

    private fun getPollutionDetailsFromArray(data: Array<Int>): PollutionDetails {
        return PollutionDetails(data[0], data[1], data[2], data[3], data[4], data[5], data[6])
    }
}