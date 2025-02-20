package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.NewPartCategories
import com.prodacc.data.remote.dao.NewProduct
import com.prodacc.data.remote.dao.NewProductVehicle
import com.prodacc.data.remote.dao.PartCategories
import com.prodacc.data.remote.dao.Product
import com.prodacc.data.remote.dao.ProductVehicle
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface ProductService {
    @GET("/products/all")
    suspend fun getProducts(): Response<List<Product>>

    @GET("/products/categories")
    suspend fun getCategories(): Response<List<PartCategories>>

    @GET("/products/vehicles")
    suspend fun getVehicleList(): Response<List<ProductVehicle>>

    @POST("/products/add")
    suspend fun addNewProduct(@Body product: NewProduct): Response<Product>

    @POST("/products/vehicles/add")
    suspend fun addNewVehicle(@Body vehicle: NewProductVehicle): Response<ProductVehicle>

    @POST("products/categories/add")
    suspend fun addNewCategory(@Body categories: NewPartCategories): Response<PartCategories>

    @DELETE("/products/delete/{id}")
    suspend fun deleteProduct(@Path("id") id: UUID): Response<Unit>

    @DELETE("/products/vehicles/delete/{id}")
    suspend fun deleteVehicle(@Path("id") id: UUID): Response<Unit>

    @DELETE("/products/categories/delete/{id}")
    suspend fun deleteCategory(@Path("id") id: UUID): Response<Unit>

    @PUT("/products/update/{id}")
    suspend fun updateProduct(@Path("id") id: UUID, @Body product: Product): Response<Product>

    @PUT("/products/vehicles/update/{id}")
    suspend fun updateVehicle(@Path("id") id: UUID, @Body vehicle: ProductVehicle): Response<ProductVehicle>

    @PUT("/products/categories/update/{id}")
    suspend fun updateCategory(@Path("id") id: UUID, @Body category: PartCategories): Response<PartCategories>
}