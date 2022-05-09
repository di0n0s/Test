package com.example.myapplication

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {

    private lateinit var viewModel: UserViewModel

    private val ioDispatcher = TestCoroutineDispatcher()

    private val userId = "2"

    @Mock
    private lateinit var service: ApiService

    @Mock
    private lateinit var userResponse: UserResponse

    private val user = User(
        id = "2",
        first_name = "Salva",
        last_name = "Fuentes",
        avatar = "avatar",
        email = "email"
    )

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        viewModel = UserViewModel(id = userId, ioDispatcher)
    }

    @After
    fun tearDown() {
        Mockito.verifyNoMoreInteractions(service, userResponse)
    }

    @Test
    fun `GIVEN an user WHEN getUser is called THEN state is success`() = runBlockingTest {
        //GIVEN
        Mockito.`when`(service.getUser(userId)).thenReturn(userResponse)
        Mockito.`when`(userResponse.data).thenReturn(user)

        //WHEN
        viewModel.userState.test {
            Assert.assertEquals(GetUserState.Success(user), expectMostRecentItem())
            awaitComplete()
        }

        //THEN
        Mockito.verify(service).getUser(userId)
    }
}