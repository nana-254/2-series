package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.theme.AccentTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Trim(
    val id: String,
    val name: String,
    val modelYear: String = "2023",
    val engineCode: String,
    val engineDesc: String,
    val hp: Int,
    val torque: Int,
    val zeroToSixty: Float,
    val quarterMile: Float,
    val topSpeed: Int,
    val transmission: String,
    val drivetrain: String,
    val curbWeightLbs: Int,
    val startingPrice: Int,
    val mpgCity: Int,
    val mpgHwy: Int,
    val mpgCombined: Int,
    val cc: Int,
    val fuelTankGallons: Double,
    val heroImageUrl: String,
    val tag: String,
    val description: String,
    val kenyaCrspUsd: Double
)

data class ExteriorColor(
    val id: String,
    val name: String,
    val colorHex: Long,
    val price: Int,
    val finish: String
)

enum class ModCategory(val displayName: String) {
    AERO_BUMPER("Front Aerodynamics & Bumpers"),
    REAR_EXHAUST("Rear Diffuser & Exhaust"),
    HOOD_BODY("Carbon Hood & Widebody"),
    WHEELS_STANCE("Forged Wheels & Suspension")
}

data class ModItem(
    val id: String,
    val name: String,
    val category: ModCategory,
    val price: Int,
    val description: String,
    val hpDelta: Int = 0,
    val weightDeltaLbs: Int = 0,
    val visualFeature: String
)

data class Option(
    val id: String,
    val name: String,
    val price: Int,
    val description: String
)

val trims = listOf(
    Trim(
        id = "230i",
        name = "2023 BMW 230i M Sport",
        modelYear = "2023",
        engineCode = "B48B20O1",
        engineDesc = "2.0L BMW TwinPower Turbo Inline-4",
        hp = 255,
        torque = 295,
        zeroToSixty = 5.3f,
        quarterMile = 13.8f,
        topSpeed = 155,
        transmission = "8-Speed Steptronic Sport with Launch Control",
        drivetrain = "RWD (Rear-Wheel Drive)",
        curbWeightLbs = 3519,
        startingPrice = 38400,
        mpgCity = 26,
        mpgHwy = 35,
        mpgCombined = 29,
        cc = 1998,
        fuelTankGallons = 13.7,
        heroImageUrl = "https://images.unsplash.com/photo-1555099962-4199c345e5dd?q=80&w=800&auto=format&fit=crop",
        tag = "Daily Agility & Mod Canvas",
        description = "Lightweight front axle, crisp 50:50 balance, and 1,998cc displacement qualifying for Kenya's lower 25% excise tax bracket.",
        kenyaCrspUsd = 46500.0
    ),
    Trim(
        id = "m240i",
        name = "2023 BMW M240i xDrive",
        modelYear = "2023",
        engineCode = "B58B30O1",
        engineDesc = "3.0L BMW M TwinPower Turbo Inline-6",
        hp = 382,
        torque = 369,
        zeroToSixty = 4.1f,
        quarterMile = 12.3f,
        topSpeed = 155,
        transmission = "8-Speed Sport Auto with Sprint Mode & M Sport Diff",
        drivetrain = "xDrive Rear-Biased All-Wheel Drive",
        curbWeightLbs = 3871,
        startingPrice = 49900,
        mpgCity = 23,
        mpgHwy = 32,
        mpgCombined = 26,
        cc = 2998,
        fuelTankGallons = 13.7,
        heroImageUrl = "https://images.unsplash.com/photo-1580273916550-e323be2ae537?q=80&w=800&auto=format&fit=crop",
        tag = "All-Weather B58 Rocket",
        description = "Legendary B58 inline-6 acoustic rumble, brutal xDrive launch traction, and near-supercar acceleration in an everyday coupe.",
        kenyaCrspUsd = 62500.0
    ),
    Trim(
        id = "m2",
        name = "2023 BMW M2 Coupe (G87)",
        modelYear = "2023",
        engineCode = "S58B30T0",
        engineDesc = "3.0L M TwinPower Twin-Turbo Inline-6",
        hp = 453,
        torque = 406,
        zeroToSixty = 3.9f,
        quarterMile = 11.8f,
        topSpeed = 177,
        transmission = "8-Speed M Steptronic w/ Drivelogic",
        drivetrain = "RWD w/ Active M Diff & 10-Stage M Traction",
        curbWeightLbs = 3814,
        startingPrice = 63200,
        mpgCity = 16,
        mpgHwy = 24,
        mpgCombined = 19,
        cc = 2993,
        fuelTankGallons = 13.7,
        heroImageUrl = "https://images.unsplash.com/photo-1616422285623-13ff0162193c?q=80&w=800&auto=format&fit=crop",
        tag = "Uncompromising Pure M Beast",
        description = "Track-conquering widebody aerodynamics, carbon fiber roof, 6-piston fixed M compound brakes, and pure rear-wheel drive thrills.",
        kenyaCrspUsd = 81000.0
    )
)

val exteriorColors = listOf(
    ExteriorColor("thundernight", "Thundernight Metallic", 0xFF4B184E, 650, "Metallic"),
    ExteriorColor("brooklyn", "Brooklyn Grey Metallic", 0xFF8A939C, 650, "Metallic"),
    ExteriorColor("isle_of_man", "Isle of Man Green", 0xFF0E7A53, 1200, "M Metallic"),
    ExteriorColor("zandvoort", "Zandvoort Blue", 0xFF7CA6D8, 650, "M Specific"),
    ExteriorColor("toronto_red", "Toronto Red Metallic", 0xFFC91A25, 650, "M Metallic"),
    ExteriorColor("portimao", "Portimao Blue", 0xFF173E84, 650, "Metallic"),
    ExteriorColor("alpine_white", "Alpine White", 0xFFF0F2F5, 0, "Non-Metallic"),
    ExteriorColor("black_sapphire", "Black Sapphire Metallic", 0xFF141416, 650, "Metallic"),
    ExteriorColor("frozen_pure_grey", "Frozen Pure Grey Matte", 0xFF636A73, 2350, "Frozen Matte"),
    ExteriorColor("satin_carbon_wrap", "Satin Stealth Carbon Wrap", 0xFF20232A, 2800, "Custom Wrap")
)

val modCatalog = listOf(
    // Front Aerodynamics
    ModItem(
        id = "m2_cs_bumper",
        name = "M2 Competition Aggressive Bumper Conversion",
        category = ModCategory.AERO_BUMPER,
        price = 1850,
        description = "Replaces 230i stock front with the aggressive M2 G87-style square dual intake vents, widened front cooling ducts, and integrated gloss black kidney surround.",
        hpDelta = 0,
        weightDeltaLbs = -4,
        visualFeature = "M2 Boxy Aerodynamic Front Conversion"
    ),
    ModItem(
        id = "csl_carbon_lip",
        name = "CSL Dry Carbon Fiber Front Splitter",
        category = ModCategory.AERO_BUMPER,
        price = 890,
        description = "Aerodynamically tuned 3-piece dry carbon front lip splitter adding 45 kg of high-speed front downforce.",
        hpDelta = 0,
        weightDeltaLbs = -6,
        visualFeature = "CSL Carbon Lower Splitter"
    ),
    ModItem(
        id = "vorsteiner_fascia",
        name = "Vorsteiner VRS Aero Carbon Grille & Ducts",
        category = ModCategory.AERO_BUMPER,
        price = 1450,
        description = "Motorsport open-mesh carbon intake nostrils and side brake cooling ducts for aggressive airflow.",
        hpDelta = 0,
        weightDeltaLbs = -5,
        visualFeature = "Vorsteiner Carbon Vented Fascia"
    ),

    // Rear Aero & Exhaust
    ModItem(
        id = "m2_quad_exhaust",
        name = "M2-Style Valved Quad Titanium Exhaust & Carbon Diffuser",
        category = ModCategory.REAR_EXHAUST,
        price = 1950,
        description = "Converts 230i single/dual outlets into quad 90mm burnt titanium exhaust tips with high-flow active acoustic valves and deep 4-fin carbon diffuser.",
        hpDelta = 12,
        weightDeltaLbs = -18,
        visualFeature = "Quad Titanium Tips & 4-Fin Carbon Diffuser"
    ),
    ModItem(
        id = "carbon_cs_spoiler",
        name = "M Performance Carbon High-Kick Trunk Spoiler",
        category = ModCategory.REAR_EXHAUST,
        price = 680,
        description = "Extended high-kick carbon ducktail spoiler providing increased rear stability at 100+ mph.",
        hpDelta = 0,
        weightDeltaLbs = -3,
        visualFeature = "High-Kick Carbon Ducktail"
    ),
    ModItem(
        id = "gts_oled_taillights",
        name = "GTS Laser/OLED Matrix Smoked Tail Lights",
        category = ModCategory.REAR_EXHAUST,
        price = 920,
        description = "Signature OLED dynamic unlock sequence with smoked motorsport lenses.",
        hpDelta = 0,
        weightDeltaLbs = 0,
        visualFeature = "GTS Smoked Matrix OLED"
    ),

    // Hood & Widebody
    ModItem(
        id = "m2_powerdome_hood",
        name = "M2 G87 Carbon Powerdome Vented Hood",
        category = ModCategory.HOOD_BODY,
        price = 1850,
        description = "Dual-vented lightweight dry carbon hood with distinctive center powerdome bulge and heat extraction vents.",
        hpDelta = 0,
        weightDeltaLbs = -22,
        visualFeature = "Carbon Powerdome Vented Hood"
    ),
    ModItem(
        id = "m_widebody_fenders",
        name = "M Extended Widebody Fender Flare Kit (+35mm)",
        category = ModCategory.HOOD_BODY,
        price = 1600,
        description = "Widened front and rear arches echoing the aggressive G87 M2 stance, accommodating wider track widths.",
        hpDelta = 0,
        weightDeltaLbs = 4,
        visualFeature = "+35mm Widebody Muscular Stance"
    ),

    // Wheels & Suspension
    ModItem(
        id = "m_forged_963m",
        name = "19\"/20\" M Performance Forged Style 963M (Gunmetal)",
        category = ModCategory.WHEELS_STANCE,
        price = 3400,
        description = "Milled forged aluminum wheels shedding 3.2 kg of unsprung rotational mass per corner wrapped in Michelin Pilot Sport 4S.",
        hpDelta = 0,
        weightDeltaLbs = -28,
        visualFeature = "Staggered 19\"/20\" Forged 963M"
    ),
    ModItem(
        id = "bbs_fir_bronze",
        name = "19\" BBS FI-R Monobloc Satin Bronze",
        category = ModCategory.WHEELS_STANCE,
        price = 4600,
        description = "Iconic cross-spoke motorsport wheels with relief holes machined into the spokes. Ultra-light 7.7 kg per wheel.",
        hpDelta = 0,
        weightDeltaLbs = -34,
        visualFeature = "BBS FI-R Satin Bronze"
    ),
    ModItem(
        id = "hr_lowering_springs",
        name = "H&R Sport Lowering Springs (-25mm Front / -20mm Rear)",
        category = ModCategory.WHEELS_STANCE,
        price = 390,
        description = "Eliminates fender wheel gap, lowers center of gravity, and sharpens turn-in response without harsh ride quality.",
        hpDelta = 0,
        weightDeltaLbs = 0,
        visualFeature = "Aggressive -25mm Stance"
    ),
    ModItem(
        id = "kw_v3_coilovers",
        name = "KW Variant 3 Inox-Line Adjustable Coilovers",
        category = ModCategory.WHEELS_STANCE,
        price = 2750,
        description = "Full motorsport-grade damper kit with independently adjustable compression (12 clicks) and rebound damping (16 clicks).",
        hpDelta = 0,
        weightDeltaLbs = -8,
        visualFeature = "Track-Ready Stance & Cornering"
    )
)

val options = listOf(
    Option("m_sport_pro", "M Sport Package Pro (M Shadowline, Red Calipers, M Seat Belts)", 2950, "Full shadowline trim with extended gloss black elements and M Sport braking package"),
    Option("premium", "Executive Premium Package (Live Cockpit Pro, HUD, Harman Kardon)", 1850, "Curved display with head-up projection and 14-speaker Harman Kardon sound"),
    Option("driving_assist", "Driving Assistance Professional (Active Cruise with Stop & Go)", 1700, "Radar-guided cruise control with highway lane change assist"),
    Option("carbon_roof", "M Carbon Fiber Lightweight Roof Panel (-6 kg)", 2800, "Lowers center of gravity and accentuates motorsport aesthetic")
)

enum class Currency(val symbol: String, val rateFromUsd: Double, val code: String) {
    KSH("KSh", 130.0, "KSh"),
    USD("$", 1.0, "USD"),
    USDT("USDT", 1.0, "USDT"),
    BTC("BTC", 0.000016, "BTC"),
    SOL("SOL", 0.0065, "SOL"),
    MONERO("XMR", 0.0072, "XMR")
}

data class KenyaImportCalculation(
    val cifUsd: Double,
    val customsValueKsh: Double,
    val importDutyKsh: Double,
    val exciseDutyRatePercent: Int,
    val exciseDutyKsh: Double,
    val vatKsh: Double,
    val idfKsh: Double,
    val rdlKsh: Double,
    val portAndRegistrationKsh: Double,
    val totalTaxesKsh: Double,
    val totalLandedCostKsh: Double,
    val totalLandedCostUsd: Double
)

data class AppState(
    val selectedTrimId: String = "230i",
    val selectedColorId: String = "thundernight",
    val selectedOptionIds: Set<String> = setOf("m_sport_pro"),
    val selectedModIds: Set<String> = setOf("m2_cs_bumper", "m2_quad_exhaust", "hr_lowering_springs"),
    val selectedAccentTheme: AccentTheme = AccentTheme.M_MOTORSPORT,
    val customAccentHex: Long? = null,
    val selectedCurrency: Currency = Currency.KSH,
    val tcoYears: Int = 5,
    val annualMileage: Int = 12000,
    val gasPricePerGallon: Double = 3.85,
    val annualMileageKm: Int = 18000,
    val fuelPricePerLiterKsh: Double = 186.0,
    val useMetricUnits: Boolean = true,
    val chatHistory: List<ChatMessage> = emptyList(),
    val isChatLoading: Boolean = false,
    val generatedImageBase64: String? = null,
    val generatedVideoUrl: String? = null,
    val isMediaLoading: Boolean = false,
    val mediaError: String? = null
)

data class ChatMessage(val text: String, val isUser: Boolean)

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    fun selectTrim(id: String) {
        _state.value = _state.value.copy(selectedTrimId = id)
    }

    fun selectColor(colorId: String) {
        _state.value = _state.value.copy(selectedColorId = colorId)
    }

    fun toggleMod(modId: String) {
        val current = _state.value.selectedModIds
        val newSet = if (current.contains(modId)) current - modId else current + modId
        _state.value = _state.value.copy(selectedModIds = newSet)
    }

    fun toggleOption(id: String) {
        val current = _state.value.selectedOptionIds
        val newSet = if (current.contains(id)) current - id else current + id
        _state.value = _state.value.copy(selectedOptionIds = newSet)
    }

    fun setAccentTheme(theme: AccentTheme) {
        _state.value = _state.value.copy(selectedAccentTheme = theme)
    }

    fun setCustomAccentHex(hex: Long?) {
        _state.value = _state.value.copy(customAccentHex = hex)
    }

    fun setCurrency(currency: Currency) {
        _state.value = _state.value.copy(selectedCurrency = currency)
    }

    fun setTcoYears(years: Int) {
        _state.value = _state.value.copy(tcoYears = years)
    }

    fun setAnnualMileage(mileage: Int) {
        _state.value = _state.value.copy(
            annualMileage = mileage,
            annualMileageKm = (mileage * 1.60934).toInt()
        )
    }

    fun setGasPrice(price: Double) {
        _state.value = _state.value.copy(gasPricePerGallon = price)
    }

    fun setAnnualMileageKm(mileageKm: Int) {
        _state.value = _state.value.copy(
            annualMileageKm = mileageKm,
            annualMileage = (mileageKm / 1.60934).toInt()
        )
    }

    fun setFuelPricePerLiterKsh(priceKsh: Double) {
        _state.value = _state.value.copy(fuelPricePerLiterKsh = priceKsh)
    }

    fun toggleUnits() {
        _state.value = _state.value.copy(useMetricUnits = !_state.value.useMetricUnits)
    }

    // Kenya Revenue Authority (KRA) Duty and Landed Cost Calculation
    fun calculateKenyaImport(trim: Trim, basePriceUsd: Double): KenyaImportCalculation {
        val exchangeRate = 130.0 // USD to KSh
        val shippingAndInsuranceUsd = 2150.0 // FOB to Mombasa Port ocean freight + marine transit insurance
        val cifUsd = basePriceUsd + shippingAndInsuranceUsd
        val customsValueKsh = cifUsd * exchangeRate

        // 1. Import Duty (ID): 35% of Customs Value (EAC CET tariff)
        val importDutyKsh = customsValueKsh * 0.35

        // 2. Excise Duty (ED): 25% for <= 2,000 cc (230i: 1998cc), 35% for > 2,000 cc (M240i: 2998cc, M2: 2993cc)
        val exciseRate = if (trim.cc <= 2000) 0.25 else 0.35
        val exciseDutyRatePercent = (exciseRate * 100).toInt()
        val exciseDutyKsh = (customsValueKsh + importDutyKsh) * exciseRate

        // 3. VAT: 16% on (Customs Value + Import Duty + Excise Duty)
        val vatKsh = (customsValueKsh + importDutyKsh + exciseDutyKsh) * 0.16

        // 4. Import Declaration Fee (IDF): 2.5% of CIF
        val idfKsh = customsValueKsh * 0.025

        // 5. Railway Development Levy (RDL): 2.0% of CIF
        val rdlKsh = customsValueKsh * 0.020

        // 6. Port charges, CFS, Radiation, KEBS, NTSA registration plate fees (~KSh 110,000)
        val portAndRegistrationKsh = 110000.0

        val totalTaxesKsh = importDutyKsh + exciseDutyKsh + vatKsh + idfKsh + rdlKsh + portAndRegistrationKsh
        val totalLandedCostKsh = customsValueKsh + totalTaxesKsh
        val totalLandedCostUsd = totalLandedCostKsh / exchangeRate

        return KenyaImportCalculation(
            cifUsd = cifUsd,
            customsValueKsh = customsValueKsh,
            importDutyKsh = importDutyKsh,
            exciseDutyRatePercent = exciseDutyRatePercent,
            exciseDutyKsh = exciseDutyKsh,
            vatKsh = vatKsh,
            idfKsh = idfKsh,
            rdlKsh = rdlKsh,
            portAndRegistrationKsh = portAndRegistrationKsh,
            totalTaxesKsh = totalTaxesKsh,
            totalLandedCostKsh = totalLandedCostKsh,
            totalLandedCostUsd = totalLandedCostUsd
        )
    }

    fun sendChatMessage(message: String) {
        val currentChat = _state.value.chatHistory.toMutableList()
        currentChat.add(ChatMessage(message, true))
        _state.value = _state.value.copy(chatHistory = currentChat, isChatLoading = true)

        viewModelScope.launch {
            val response = generateChatResponse(currentChat)
            val updatedChat = _state.value.chatHistory.toMutableList()
            updatedChat.add(ChatMessage(response, false))
            _state.value = _state.value.copy(chatHistory = updatedChat, isChatLoading = false)
        }
    }

    private suspend fun generateChatResponse(history: List<ChatMessage>): String = withContext(Dispatchers.IO) {
        try {
            val contents = history.map { msg ->
                Content(
                    role = if (msg.isUser) "user" else "model",
                    parts = listOf(Part(text = msg.text))
                )
            }
            val request = GenerateContentRequest(
                contents = contents,
                systemInstruction = Content(parts = listOf(Part(text = "You are an expert BMW 2 Series (230i M Sport, M240i, M2 G87) automotive and modifications specialist. You also have deep expertise in vehicle importation into Kenya, KRA duty rates, B48 vs B58 vs S58 tuning, body kit modifications, and performance statistics."))),
                generationConfig = GenerationConfig(thinkingConfig = ThinkingConfig("HIGH")),
                tools = listOf(Tool(googleSearch = emptyMap(), googleMaps = emptyMap()))
            )
            val response = RetrofitClient.service.generateContent(
                model = "gemini-3.1-pro-preview",
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I am sorry, I could not process that request."
        } catch (e: Exception) {
            e.printStackTrace()
            "Error communicating with AI assistant: ${e.message}"
        }
    }

    fun generateImage(prompt: String, aspect: String, size: String) {
        _state.value = _state.value.copy(isMediaLoading = true, mediaError = null, generatedImageBase64 = null)
        viewModelScope.launch {
            try {
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(
                        responseModalities = listOf("TEXT", "IMAGE"),
                        imageConfig = ImageConfig(aspectRatio = aspect, imageSize = size)
                    )
                )
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateContent(
                        model = "gemini-3-pro-image-preview",
                        apiKey = BuildConfig.GEMINI_API_KEY,
                        request = request
                    )
                }
                val inlineData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData
                if (inlineData != null) {
                    _state.value = _state.value.copy(generatedImageBase64 = inlineData.data, isMediaLoading = false)
                } else {
                    _state.value = _state.value.copy(mediaError = "Failed to extract image from response.", isMediaLoading = false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(mediaError = "Image Gen Error: ${e.message}", isMediaLoading = false)
            }
        }
    }

    fun generateVideo(prompt: String, aspect: String) {
        _state.value = _state.value.copy(isMediaLoading = true, mediaError = null, generatedVideoUrl = null)
        viewModelScope.launch {
            try {
                val request = GenerateVideosRequest(
                    prompt = prompt,
                    config = VeoConfig(numberOfVideos = 1, resolution = "1080p", aspectRatio = aspect)
                )
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.service.generateVideos(
                        model = "veo-3.1-fast-generate-preview",
                        apiKey = BuildConfig.GEMINI_API_KEY,
                        request = request
                    )
                }
                _state.value = _state.value.copy(generatedVideoUrl = "Video requested successfully! Operation: $response", isMediaLoading = false)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(mediaError = "Video Gen Error: ${e.message}", isMediaLoading = false)
            }
        }
    }
}
