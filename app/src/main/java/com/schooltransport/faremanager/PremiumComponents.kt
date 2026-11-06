
@Composable
fun PremiumAppHeader(
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(
                    bottomStart = 25.dp,
                    bottomEnd = 25.dp
                ),
                ambientColor = PremiumDeepPurple.copy(alpha = 0.25f),
                spotColor = PremiumDeepPurple.copy(alpha = 0.35f)
            ),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 25.dp,
            bottomEnd = 25.dp
        ),
        color = PremiumRoyalPurple
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            PremiumDeepPurple,
                            PremiumRoyalPurple,
                            PremiumPurple
                        )
                    )
                )
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 21.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "School Transport",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    "Fare Manager",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PremiumGold
                )
            }

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .shadow(5.dp, CircleShape)
                    .clip(CircleShape)
                    .background(PremiumCream)
                    .border(2.dp, PremiumGold, CircleShape)
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Open Profile",
                    modifier = Modifier.size(39.dp),
                    tint = PremiumRoyalPurple
                )
            }
        }
    }
}
