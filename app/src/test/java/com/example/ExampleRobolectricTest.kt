package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.MemberCardEntity
import com.example.data.MemberCardRepository
import com.example.util.AadhaarOcrParser
import com.example.util.DistrictCodeHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: MemberCardRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MemberCardRepository(db.memberCardDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TN Painters ID", appName)
    }

    @Test
    fun `insert and retrieve member card`() = runBlocking {
        val card = MemberCardEntity(
            memberId = "TN-MDU-0001",
            name = "ரா. செந்தில்குமார்",
            jobTitle = "ஓவியர்",
            district = "மதுரை",
            cardType = "MEMBER"
        )
        val id = repository.saveCard(card)
        assertNotNull(id)

        val retrieved = repository.getCardById(id).first()
        assertNotNull(retrieved)
        assertEquals("TN-MDU-0001", retrieved?.memberId)
        assertEquals("ரா. செந்தில்குமார்", retrieved?.name)
        assertEquals("மதுரை", retrieved?.district)
    }

    @Test
    fun `test district code starting from 0001 and sequential increment`() {
        // Madurai Member ID starting from 0001
        val madurai0001 = DistrictCodeHelper.generateDistrictMemberId("மதுரை", "MEMBER", 1)
        assertEquals("TN-MDU-0001", madurai0001)

        // Chennai Member ID starting from 0001
        val chennai0001 = DistrictCodeHelper.generateDistrictMemberId("சென்னை", "MEMBER", 1)
        assertEquals("TN-CHN-0001", chennai0001)

        // Executive Madurai
        val exec0001 = DistrictCodeHelper.generateDistrictMemberId("மதுரை", "EXECUTIVE", 1)
        assertEquals("TN-EXEC-MDU-0001", exec0001)

        // Contractor Dindigul
        val con0001 = DistrictCodeHelper.generateDistrictMemberId("திண்டுக்கல்", "CONTRACTOR", 1)
        assertEquals("TN-CON-DGL-0001", con0001)

        // Sequential increment test
        val existingCards = listOf(
            MemberCardEntity(memberId = "TN-MDU-0001", name = "Test 1", district = "மதுரை"),
            MemberCardEntity(memberId = "TN-MDU-0002", name = "Test 2", district = "மதுரை")
        )
        val nextMadurai = DistrictCodeHelper.getNextMemberIdForDistrict("மதுரை", "MEMBER", existingCards)
        assertEquals("TN-MDU-0003", nextMadurai)
    }

    @Test
    fun `test search by member name and registration number`() = runBlocking {
        repository.saveCard(MemberCardEntity(memberId = "TN-MDU-0001", name = "மு. கார்த்திகேயன்", district = "மதுரை"))
        repository.saveCard(MemberCardEntity(memberId = "TN-CHN-0001", name = "க. மாரிமுத்து", district = "சென்னை"))
        repository.saveCard(MemberCardEntity(memberId = "TN-CBE-0001", name = "வே. சுப்பிரமணி", district = "கோயம்புத்தூர்"))

        val all = repository.allCards.first()

        // Search by name
        val nameSearch = all.filter { it.name.contains("கார்த்திகேயன்") }
        assertEquals(1, nameSearch.size)
        assertEquals("TN-MDU-0001", nameSearch.first().memberId)

        // Search by registration number
        val idSearch = all.filter { it.memberId.contains("0001") }
        assertEquals(3, idSearch.size)
    }

    @Test
    fun `test aadhaar ocr parser text recognition`() {
        val sampleAadhaar = """
            GOVERNMENT OF INDIA
            Unique Identification Authority of India
            மு. கார்த்திகேயன்
            DOB: 15/06/1990
            Male
            S/O: முத்துசாமி
            1/14 அம்பலக்காரன் பட்டி, உத்தங்குடி, மதுரை 625107
            1234 5678 9012
        """.trimIndent()

        val ocrResult = AadhaarOcrParser.parseAadhaarText(sampleAadhaar, "MEMBER")
        assertEquals("மு. கார்த்திகேயன்", ocrResult.name)
        assertEquals("முத்துசாமி", ocrResult.fatherName)
        assertEquals("15/06/1990", ocrResult.dob)
        assertEquals("மதுரை", ocrResult.district)
        assertEquals("TN-MDU-0001", ocrResult.generatedMemberId)
    }

    @Test
    fun `test card bitmap rendering and dual sheet export creation`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sampleCard = MemberCardEntity(
            memberId = "TN-MDU-0001",
            name = "மு. கார்த்திகேயன்",
            jobTitle = "வண்ணப் பூச்சாளர்",
            district = "மதுரை",
            fatherName = "முத்துசாமி",
            age = "34",
            bloodGroup = "O +ve"
        )

        val frontBmp = com.example.util.BitmapRendererHelper.renderCardToBitmap(context, sampleCard, isBack = false)
        assertNotNull(frontBmp)
        assertTrue(frontBmp.width > 0)
        assertTrue(frontBmp.height > 0)

        val backBmp = com.example.util.BitmapRendererHelper.renderCardToBitmap(context, sampleCard, isBack = true)
        assertNotNull(backBmp)

        val dualSheet = com.example.util.CardExporter.generateDualSideSheetBitmap(frontBmp, backBmp)
        assertNotNull(dualSheet)
        assertTrue(dualSheet.width >= frontBmp.width)
        assertTrue(dualSheet.height >= frontBmp.height + backBmp.height)
    }

    @Test
    fun `test qr code generator payload encoding and verification parsing`() {
        val sampleCard = MemberCardEntity(
            memberId = "TN-MDU-1024",
            name = "மு. கார்த்திகேயன்",
            district = "மதுரை",
            phone = "9876543210",
            bloodGroup = "O +ve",
            approvalStatus = "APPROVED"
        )

        val payload = com.example.util.QRCodeGenerator.createMemberVerificationPayload(sampleCard)
        assertTrue(payload.contains("TN-MDU-1024"))
        assertTrue(payload.contains("மு. கார்த்திகேயன்"))
        assertTrue(payload.contains("மதுரை"))

        val qrBmp = com.example.util.QRCodeGenerator.generateCardQrBitmap(sampleCard, 150)
        assertNotNull(qrBmp)
        assertEquals(150, qrBmp.width)
        assertEquals(150, qrBmp.height)

        val parsed = com.example.util.QRCodeGenerator.parseVerificationPayload(payload)
        assertEquals("TN-MDU-1024", parsed["ID"])
        assertEquals("மு. கார்த்திகேயன்", parsed["NAME"])
        assertEquals("மதுரை", parsed["DIST"])
        assertEquals("APPROVED", parsed["STATUS"])
    }

    @Test
    fun `test aadhaar ocr apply to card and custom seal entity`() {
        val baseCard = MemberCardEntity(
            memberId = "TN-MDU-0001",
            name = "பழைய பெயர்",
            district = "மதுரை"
        )
        val ocrResult = com.example.util.AadhaarOcrResult(
            name = "மு. கார்த்திகேயன்",
            fatherName = "முத்துசாமி",
            age = "34",
            dob = "15/06/1990",
            gender = "ஆண் (Male)",
            address = "1/14 அம்பலக்காரன் பட்டி, மதுரை 625107",
            district = "மதுரை",
            aadhaarNumber = "1234 5678 9012",
            generatedMemberId = "TN-MDU-1024"
        )

        val appliedCard = com.example.util.AadhaarOcrParser.applyToMemberCard(baseCard, ocrResult)
        assertEquals("TN-MDU-1024", appliedCard.memberId)
        assertEquals("மு. கார்த்திகேயன்", appliedCard.name)
        assertEquals("முத்துசாமி", appliedCard.fatherName)
        assertEquals("34", appliedCard.age)
        assertEquals("1234 5678 9012", appliedCard.aadhaarNumber)

        val withSealCard = appliedCard.copy(customSealUri = "content://media/external/images/media/999")
        assertEquals("content://media/external/images/media/999", withSealCard.customSealUri)
    }
}
