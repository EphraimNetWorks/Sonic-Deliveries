package com.example.deliveryapp.utils

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PositionalDataSource
import org.mockito.Mockito

class MockRoomDataSource<Value>(private val list: List<Value>): PageKeyedDataSource<Int,Value>(){

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Value>) {

        val endIndex = if(params.requestedLoadSize>list.size) list.size else params.requestedLoadSize

        callback.onResult(list.subList(0,endIndex),null,2)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Value>) {

        try {
            val startIndex = (params.key - 1)*params.requestedLoadSize
            val endIndex = params.key*params.requestedLoadSize
            callback.onResult(list.subList(startIndex, endIndex),params.key+1)
        }catch (e:IndexOutOfBoundsException){
            e.printStackTrace()
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Value>) {

    }

    companion object{

        fun <Value> mockDataSourceFactory(list: List<Value>): DataSource.Factory<Int,Value> {
            val dataSourceFactory = Mockito.mock(DataSource.Factory::class.java) as DataSource.Factory<Int,Value>
            val dataSource = MockRoomDataSource(list)

            Mockito.`when`(dataSourceFactory.create()).thenReturn(dataSource)

            return dataSourceFactory
        }
    }
}