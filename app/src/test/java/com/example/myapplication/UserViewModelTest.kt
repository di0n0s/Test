package com.example.myapplication

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {

    private lateinit var viewModel: UserViewModel

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var userResponse: UserResponse

    private val ioDispatcher = TestCoroutineDispatcher()

    private val userId = "2"

    private val user = User(
        id = userId,
        email = "email",
        first_name = "first_name",
        last_name = "last_name",
        avatar = "avatar"
    )

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        viewModel = UserViewModel(apiService = apiService, iODispatcher = ioDispatcher)
    }

    @Test
    fun `GIVEN an user WHEN GetUserIntent is sent THEN userState is Success`() = runBlockingTest {
        //GIVEN
        Mockito.`when`(apiService.getUser(userId)).thenReturn(userResponse)
        Mockito.`when`(userResponse.data).thenReturn(user)

        val result = arrayListOf<GetUserState>()
        val job = launch {
            viewModel.userState.toList(result)
        }

        //WHEN
        viewModel.userIntent.send(UserIntent.GetUser(userId))

        //THEN
        Assert.assertEquals(result.first(), GetUserState.Idle)
        Assert.assertEquals(result[1], GetUserState.Loading)
        MatcherAssert.assertThat(result[2], instanceOf(GetUserState.Success::class.java))
        Assert.assertEquals((result[2] as GetUserState.Success).user, user)


        Mockito.verify(apiService).getUser(userId)
        job.cancel()
    }

    @Test
    fun `GIVEN an exception WHEN GetUserIntent is sent THEN userState is Error`() =
        runBlockingTest {
            //GIVEN
            val errorText = "There was an error"
            val exception = RuntimeException(errorText)
            Mockito.`when`(apiService.getUser(userId)).thenThrow(exception)

            val result = arrayListOf<GetUserState>()
            val job = launch {
                viewModel.userState.toList(result)
            }

            //WHEN
            viewModel.userIntent.send(UserIntent.GetUser(userId))

            //THEN
            Assert.assertEquals(result.first(), GetUserState.Idle)
            Assert.assertEquals(result[1], GetUserState.Loading)
            MatcherAssert.assertThat(result[2], instanceOf(GetUserState.Error::class.java))
            Assert.assertEquals((result[2] as GetUserState.Error).error, exception.localizedMessage)


            Mockito.verify(apiService).getUser(userId)
            job.cancel()
        }

    @After
    fun tearDown() {
        Mockito.verifyNoMoreInteractions(apiService)
    }
}