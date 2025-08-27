package com.kh.daily.presentation.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kh.daily.R
import com.kh.daily.presentation.intro.IntroPage

@Composable
fun AppIntroScreen(onFinish: () -> Unit){
//    val pagerState =  rememberPagerState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        HorizontalPager(
//            state = pagerState,
//            modifier = Modifier.weight(1f)
//        ) { page ->
//            val introPage = introPages[page]
//            IntroPageContent(introPage)
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        PagerIndicator(pagerState = pagerState, pageCount = introPages.size)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                if (pagerState.currentPage < introPages.size - 1) {
//                    // Move to the next page
//                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
//                } else {
//                    // Finish the intro
//                    onFinish()
//                }
//            }
//        ) {
//            Text(
//                text = if (pagerState.currentPage == introPages.size - 1) "Get Started" else "Next"
//            )
//        }
//    }


}

val introPages = listOf(
    IntroPage("Welcome", "Discover new features in our app.", R.drawable.intro),
    IntroPage("Stay Connected", "Connect with people seamlessly.", R.drawable.intro),
    IntroPage("Get Started", "Start your journey with us today!", R.drawable.intro)
)

@Composable
fun IntroPageContent(page: IntroPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PagerIndicator(pagerState: PagerState, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(pageCount) { index ->
            val color = if (pagerState.currentPage == index) Color.Blue else Color.Gray
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .padding(2.dp)
                    .background(color, shape = CircleShape)
            )
        }
    }
}