package com.example.endangeredanimals

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.endangeredanimals.data.DataSource
import com.example.endangeredanimals.model.Animal
import com.example.endangeredanimals.ui.theme.CatskillWhite
import com.example.endangeredanimals.ui.theme.EndangeredAnimalsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EndangeredAnimalsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimalsList(DataSource().getAnimalsList(), Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AnimalsList(animals: List<Animal>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(animals) {
            AnimalsListItem(animal = it)
        }
    }

}

@Composable
fun AnimalsListItem(animal: Animal, modifier: Modifier = Modifier) {

    //State Hoisting
    var isDialogShown by remember { mutableStateOf(false) }

    if (isDialogShown)
        AnimalHelperDialog(animal = animal) {
            isDialogShown = it

        }
    Card(
        colors = CardDefaults.cardColors(containerColor = CatskillWhite),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = animal.image),
                contentDescription = stringResource(
                    id = animal.name
                ),
                modifier = modifier.size(120.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    //Note: height is related to "weight"
                    .height(120.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = animal.name),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        //The parent will divide the vertical space remaining
                        //after measuring unweighted child elements
                        .weight(1f)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
                //This text will take its default size, then the
                //above text will take the remaining space
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(
                                stringResource(
                                    id = R.string.save_the_animal,
                                    stringResource(id = R.string.save), "", ""
                                )
                            )
                        }
                        append(
                            stringResource(
                                id = R.string.save_the_animal,
                                "", stringResource(id = R.string.the_animal), ""
                            )
                        )
                    },
                    modifier = modifier.clickable { isDialogShown = true }
                )
            }
        }
    }
}

@Composable
fun AnimalHelperDialog(animal: Animal, onShowModifier: (Boolean) -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {

        },
        confirmButton = {
            TextButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, animal.link.toUri())
                context.startActivity(intent)
                onShowModifier(false)
            }) {
                Text(text = stringResource(id = R.string.proceed))
            }
        },
        dismissButton = {
            TextButton(onClick = { onShowModifier(false) }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_help),
                contentDescription = stringResource(
                    id = R.string.help
                )
            )
        },
        text = {
            Text(text = stringResource(id = animal.description))
        },
        title = {
            Text(
                text = stringResource(
                    id = R.string.save_the_animal,
                    stringResource(id = R.string.save),
                    stringResource(id = R.string.the_animal), ("?")
                )
            )
        }

    )
}

@Preview(showSystemUi = true)
@Composable
private fun AnimalItemPreview() {
    AnimalsList(animals = DataSource().getAnimalsList())

}
