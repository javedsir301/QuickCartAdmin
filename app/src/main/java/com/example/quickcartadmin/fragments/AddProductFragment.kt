package com.example.quickcartadmin.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.quickcartadmin.utils.Constants
import com.example.quickcartadmin.R
import com.example.quickcartadmin.utils.Utils
import com.example.quickcartadmin.activity.AdminMainActivity
import com.example.quickcartadmin.adapter.AdapterSelectedImages
import com.example.quickcartadmin.databinding.FragmentAddProductBinding
import com.example.quickcartadmin.models.Product
import com.example.quickcartadmin.viewmodel.AdminViewModel
import kotlinx.coroutines.launch


class AddProductFragment : Fragment() {

    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentAddProductBinding
    private val imageUris: ArrayList<Uri> = arrayListOf()
    private val selectedImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listOfUri ->
            val fiveImages = listOfUri.take(5)
            imageUris.clear()
            imageUris.addAll(fiveImages)
            binding.rvProductImages.adapter = AdapterSelectedImages(imageUris)

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(layoutInflater)

        setAutoCompleteTextView()
        onImageSelectClick()
        onAddButtonClick()

        return binding.root

    }


    private fun onAddButtonClick() {
        binding.btnAddProduct.setOnClickListener {
            Utils.showDialog(requireContext(), "Uploading images.....")
            val productTitle = binding.etProductTitle.text.toString()
            val productQuantity = binding.etProductQuantity.text.toString()
            val productUnit = binding.etProductUnit.text.toString()
            val productPrice = binding.etProductPrice.text.toString()
            val productStock = binding.etProductStocks.text.toString()
            val productCategory = binding.etProductCategory.text.toString()
            val productType = binding.etProductType.text.toString()

            if (productTitle.isEmpty() || productQuantity.isEmpty()
                || productUnit.isEmpty() || productPrice.isEmpty()
                || productStock.isEmpty() || productCategory.isEmpty() || productType.isEmpty()
            ) {
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(), "Empty fields are not allowed")
                }
            } else if (imageUris.isEmpty()) {
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(), "Please upload some images")
                }
            } else {
                // Check if the product already exists
                viewModel.doesProductExist(productTitle, productCategory, productType)
                    .observe(viewLifecycleOwner) { exists ->
                        if (exists) {
                            Utils.apply {
                                hideDialog()
                                showToast(requireContext(), "Product already exists")
                            }
                        } else {
                            val product = Product(
                                productTitle = productTitle,
                                productQuantity = productQuantity.toInt(),
                                productUnit = productUnit,
                                productPrice = productPrice.toInt(),
                                productStock = productStock.toInt(),
                                productCategory = productCategory,
                                productType = productType,
                                itemCount = 0,
                                adminUid = Utils.getCurrentUserId(),
                                productRandomId = Utils.getRandomId()
                            )
                            saveImage(product)
                        }
                    }
            }
        }
    }

    private fun saveImage(product: Product) {

        viewModel.saveImageInDB(imageUris)
        lifecycleScope.launch {
            viewModel.isImageUplaoded.collect {
                if (it) {
                    Utils.apply {
                        hideDialog()
                        showToast(requireContext(), "Image Saved..")
                    }
                }
                getUrls(product)
            }
        }


    }

    private fun getUrls(product: Product) {

        Utils.showDialog(requireContext(), "Publishing products...")

        lifecycleScope.launch {
            viewModel.downloadUris.collect {
                val urls = it
                product.productImagesUris = urls
                saveProduct(product)

            }
        }

    }

    private fun saveProduct(product: Product) {
        product.itemCount = 0
        viewModel.saveProduct(product)
        lifecycleScope.launch {
            viewModel.isProductSaved.collect {
                if (it) {
                    Utils.hideDialog()
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    Utils.showToast(requireContext(), "Your Product is live ")
                }
            }
        }

    }

    private fun onImageSelectClick() {
        binding.btnSelectImage.setOnClickListener {
            selectedImages.launch("image/*")

        }
    }

    private fun setAutoCompleteTextView() {

        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProducts)
        val category =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductsCategory)
        val categoryType =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductType)

        binding.apply {
            etProductUnit.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(categoryType)
        }


    }

}