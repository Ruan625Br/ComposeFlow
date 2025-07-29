package io.composeflow.ui.string

import io.composeflow.Res
import io.composeflow.locale_name_af
import io.composeflow.locale_name_am
import io.composeflow.locale_name_bg
import io.composeflow.locale_name_ca
import io.composeflow.locale_name_cs
import io.composeflow.locale_name_da
import io.composeflow.locale_name_de
import io.composeflow.locale_name_el
import io.composeflow.locale_name_en_gb
import io.composeflow.locale_name_en_us
import io.composeflow.locale_name_es_419
import io.composeflow.locale_name_es_es
import io.composeflow.locale_name_et
import io.composeflow.locale_name_fi
import io.composeflow.locale_name_fil
import io.composeflow.locale_name_fr_ca
import io.composeflow.locale_name_fr_fr
import io.composeflow.locale_name_he
import io.composeflow.locale_name_hi
import io.composeflow.locale_name_hr
import io.composeflow.locale_name_hu
import io.composeflow.locale_name_id
import io.composeflow.locale_name_is
import io.composeflow.locale_name_it
import io.composeflow.locale_name_ja
import io.composeflow.locale_name_ko
import io.composeflow.locale_name_lt
import io.composeflow.locale_name_lv
import io.composeflow.locale_name_ms
import io.composeflow.locale_name_nl
import io.composeflow.locale_name_no
import io.composeflow.locale_name_pl
import io.composeflow.locale_name_pt_br
import io.composeflow.locale_name_pt_pt
import io.composeflow.locale_name_ro
import io.composeflow.locale_name_ru
import io.composeflow.locale_name_sk
import io.composeflow.locale_name_sl
import io.composeflow.locale_name_sr
import io.composeflow.locale_name_sv
import io.composeflow.locale_name_sw
import io.composeflow.locale_name_th
import io.composeflow.locale_name_tr
import io.composeflow.locale_name_uk
import io.composeflow.locale_name_vi
import io.composeflow.locale_name_zh_cn
import io.composeflow.locale_name_zh_hk
import io.composeflow.locale_name_zh_tw
import io.composeflow.locale_name_zu
import io.composeflow.model.project.string.ResourceLocale
import org.jetbrains.compose.resources.StringResource

val ResourceLocale.displayNameResource: StringResource
    get() =
        when (this) {
            ResourceLocale.AFRIKAANS -> Res.string.locale_name_af
            ResourceLocale.AMHARIC -> Res.string.locale_name_am
            ResourceLocale.BULGARIAN -> Res.string.locale_name_bg
            ResourceLocale.CATALAN -> Res.string.locale_name_ca
            ResourceLocale.CHINESE_HONG_KONG -> Res.string.locale_name_zh_hk
            ResourceLocale.CHINESE_PRC -> Res.string.locale_name_zh_cn
            ResourceLocale.CHINESE_TAIWAN -> Res.string.locale_name_zh_tw
            ResourceLocale.CROATIAN -> Res.string.locale_name_hr
            ResourceLocale.CZECH -> Res.string.locale_name_cs
            ResourceLocale.DANISH -> Res.string.locale_name_da
            ResourceLocale.DUTCH -> Res.string.locale_name_nl
            ResourceLocale.ENGLISH_UK -> Res.string.locale_name_en_gb
            ResourceLocale.ENGLISH_US -> Res.string.locale_name_en_us
            ResourceLocale.ESTONIAN -> Res.string.locale_name_et
            ResourceLocale.FILIPINO -> Res.string.locale_name_fil
            ResourceLocale.FINNISH -> Res.string.locale_name_fi
            ResourceLocale.FRENCH_CANADA -> Res.string.locale_name_fr_ca
            ResourceLocale.FRENCH_FRANCE -> Res.string.locale_name_fr_fr
            ResourceLocale.GERMAN -> Res.string.locale_name_de
            ResourceLocale.GREEK -> Res.string.locale_name_el
            ResourceLocale.HEBREW -> Res.string.locale_name_he
            ResourceLocale.HINDI -> Res.string.locale_name_hi
            ResourceLocale.HUNGARIAN -> Res.string.locale_name_hu
            ResourceLocale.ICELANDIC -> Res.string.locale_name_is
            ResourceLocale.INDONESIAN -> Res.string.locale_name_id
            ResourceLocale.ITALIAN -> Res.string.locale_name_it
            ResourceLocale.JAPANESE -> Res.string.locale_name_ja
            ResourceLocale.KOREAN -> Res.string.locale_name_ko
            ResourceLocale.LATVIAN -> Res.string.locale_name_lv
            ResourceLocale.LITHUANIAN -> Res.string.locale_name_lt
            ResourceLocale.MALAY -> Res.string.locale_name_ms
            ResourceLocale.NORWEGIAN -> Res.string.locale_name_no
            ResourceLocale.POLISH -> Res.string.locale_name_pl
            ResourceLocale.PORTUGUESE_BRAZIL -> Res.string.locale_name_pt_br
            ResourceLocale.PORTUGUESE_PORTUGAL -> Res.string.locale_name_pt_pt
            ResourceLocale.ROMANIAN -> Res.string.locale_name_ro
            ResourceLocale.RUSSIAN -> Res.string.locale_name_ru
            ResourceLocale.SERBIAN -> Res.string.locale_name_sr
            ResourceLocale.SLOVAK -> Res.string.locale_name_sk
            ResourceLocale.SLOVENIAN -> Res.string.locale_name_sl
            ResourceLocale.SPANISH_LATIN_AMERICA -> Res.string.locale_name_es_419
            ResourceLocale.SPANISH_SPAIN -> Res.string.locale_name_es_es
            ResourceLocale.SWAHILI -> Res.string.locale_name_sw
            ResourceLocale.SWEDISH -> Res.string.locale_name_sv
            ResourceLocale.THAI -> Res.string.locale_name_th
            ResourceLocale.TURKISH -> Res.string.locale_name_tr
            ResourceLocale.UKRAINIAN -> Res.string.locale_name_uk
            ResourceLocale.VIETNAMESE -> Res.string.locale_name_vi
            ResourceLocale.ZULU -> Res.string.locale_name_zu
        }
