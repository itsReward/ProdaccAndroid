package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.product.CreateProductCategory
import com.prodacc.data.remote.dao.product.CreateProductVehicle
import com.prodacc.data.remote.dao.product.NewProduct
import com.prodacc.data.remote.dao.product.Product
import com.prodacc.data.remote.dao.product.ProductCategory
import com.prodacc.data.remote.dao.product.ProductCategoryWithProduct
import com.prodacc.data.remote.dao.product.ProductVehicle
import com.prodacc.data.remote.dao.product.ProductVehicleWithProducts
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface ProductService {
    //Product Requests
    @GET("/products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("/products/{id}")
    suspend fun getProductById(@Path("id") id: UUID): Response<Product>

    @POST("/products/new")
    suspend fun addNewProduct(@Body product: NewProduct): Response<Product>

    @DELETE("/products/delete/{id}")
    suspend fun deleteProduct(@Path("id") id: UUID): Response<Unit>

    @PUT("/products/update/{id}")
    suspend fun updateProduct(@Path("id") id: UUID, @Body product: NewProduct): Response<Product>


    //Product Categories
    @GET("/products/categories")
    suspend fun getCategories(): Response<List<ProductCategory>>

    @GET("/products/categories/{id}")
    suspend fun getCategories(@Path("id") id: UUID): Response<ProductCategory>

    @GET("/products/categories/with-products")
    suspend fun getAllCategoriesWithProducts(): Response<List<ProductCategoryWithProduct>>

    @GET("/products/by-category/{categoryId}")
    suspend fun getProductsByCategory(@Path("categoryId") categoryId: UUID): Response<List<Product>>

    @GET("/products/categories/{categoryId}/with-products")
    suspend fun getCategoryByIdWithProducts(): Response<ProductCategoryWithProduct>

    @POST("/products/categories/new-category")
    suspend fun addNewCategory(@Body categories: CreateProductCategory): Response<ProductCategory>

    @DELETE("/products/categories/delete/{id}")
    suspend fun deleteCategory(@Path("id") id: UUID): Response<Unit>

    @PUT("/products/categories/update/{id}")
    suspend fun updateCategory(
        @Path("id") id: UUID,
        @Body category: CreateProductCategory
    ): Response<ProductCategory>


    //Product Vehicles
    @GET("/products/vehicles")
    suspend fun getVehicleList(): Response<List<ProductVehicle>>

    @GET("/products/vehicles/{vehicleId}/with-products")
    suspend fun getVehicleWithProducts(@Path("vehicleId") vehicleId: UUID): Response<ProductVehicleWithProducts>

    @GET("/products/vehicles/with-products")
    suspend fun getAllVehiclesWithProducts(): Response<List<ProductVehicleWithProducts>>

    @POST("/products/vehicles/new-vehicle")
    suspend fun addNewVehicle(@Body vehicle: CreateProductVehicle): Response<ProductVehicle>

    @DELETE("/products/vehicles/delete/{id}")
    suspend fun deleteVehicle(@Path("id") id: UUID): Response<Unit>

    @PUT("/products/vehicles/update/{id}")
    suspend fun updateVehicle(
        @Path("id") id: UUID,
        @Body vehicle: ProductVehicle
    ): Response<ProductVehicle>

    //Product Category and Vehicle Assign Operations
    @POST("/products/{productId}/categories/{categoryId}")
    fun addCategoryToProduct(
        @Path("productId") productId: UUID,
        @Path("categoryId") categoryId: UUID
    ): Response<Unit>

    @DELETE("/products/{productId}/categories/{categoryId}")
    fun removeCategoryFromProduct(
        @Path("productId") productId: UUID,
        @Path("categoryId") categoryId: UUID
    ): Response<Unit>

    @POST("/products/{productId}/vehicles/{vehicleId}")
    fun addVehicleToProduct(
        @Path("productId") productId: UUID,
        @Path("vehicleId") vehicleId: UUID
    ): Response<Unit>

    @DELETE("/products/{productId}/vehicles/{vehicleId}")
    fun removeVehicleFromProduct(
        @Path("productId") productId: UUID,
        @Path("vehicleId") vehicleId: UUID
    ): Response<Unit>
}