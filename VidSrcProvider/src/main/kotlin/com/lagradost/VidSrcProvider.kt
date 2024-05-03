package com.lagradost

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.mvvm.safeApiCall
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import org.jsoup.nodes.Element
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.metaproviders.TmdbProvider
import com.lagradost.cloudstream3.extractors.VidSrcExtractor
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import kotlin.math.roundToInt

class VidSrcProvider : MainAPI() {
    override var mainUrl = "https://vidsrc.xyz/"
    private var tmdbBaseUrl = "https://api.themoviedb.org/"
    override var name = "VidSrc"
    override val hasMainPage = true
    override var lang = "en"
    override val hasDownloadSupport = false
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries,
    )
//    private val mapper: JsonMapper = JsonMapper.builder().addModule(KotlinModule())
//        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build()


    data class Result (
        @JsonProperty("id") val id: String,
        @JsonProperty("title") val title: String,
        @JsonProperty("overview") val overview: String? = null,
        @JsonProperty("poster_path") val image: String? = null,
        @JsonProperty("media_type") val mediaType: String? = null,
    )

    data class Page (
        @JsonProperty("page") val page: Long?,
        @JsonProperty("results") val result: List<Result>?,
        @JsonProperty("total_pages") val totalPages: Long?,
        @JsonProperty("total_results") val totalResults: Long?,
    )



    override val mainPage = mainPageOf(
        "$tmdbBaseUrl/3/trending/movie/week?apikey=e1e99d44d3f10ad93c4b6873ccd92592&language=en-US&page=" to "Trending Movies"
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse? {
        val home = app.get(request.data+page).parsedSafe<Page>()?.result?.map {
                it.toSearchResponse()
            }
        return home?.let { newHomePageResponse(request.name, it) }
    }

    private fun Result.toSearchResponse(): SearchResponse{
        val url = "null"
        val name = this.title
        return newMovieSearchResponse(name,url,TvType.Movie)
    }



}