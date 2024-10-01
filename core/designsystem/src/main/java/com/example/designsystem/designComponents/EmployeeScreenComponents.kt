package com.example.designsystem.designComponents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.example.designsystem.theme.DarkGrey
import com.prodacc.data.remote.dao.Employee
import java.util.UUID


@Composable
fun EmployeeListCard(
    employee: Employee,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            ProfileAvatar(initials = "${employee.employeeName.first()}${employee.employeeSurname.first()}")
            BodyText(text = "${employee.employeeName} ${employee.employeeSurname}")
        }
        Text(text = "${employee.rating}", color = DarkGrey)

    }
}



@Composable
fun EmployeeCategoryHeader(
    text : String,
    modifier: Modifier = Modifier
){
    Row (
        modifier = Modifier.background(Color.White)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = DarkGrey

        )
    }

}


data class EmployeeListCategory(
    val name : String,
    val items : List<Employee>
)

@Composable
fun EmployeeDropDown(
    list: List<Employee>,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onItemClick: (UUID) -> Unit
){
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        list.forEach { it ->
            DropdownMenuItem(text = {
                Text(text = "${it.employeeName} ${it.employeeSurname}")
            }, onClick = { onItemClick(it.id) })
        }
    }
}

