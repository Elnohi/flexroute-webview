# FlexRoute WebView — Build via GitHub Actions

## Why this exists
Temporary WebView wrapper for Google Play closed testing.
TWA apps run inside Chrome so Google's engagement metrics don't count toward the app.
This WebView version runs flexrouteapp.com directly inside the app itself.
After production access is approved, re-upload the original TWA.

---

## How to get your signed AAB (no Android Studio needed)

### Step 1 — Create a private GitHub repository
1. Go to https://github.com/new
2. Set it to **Private**
3. Name it: `flexroute-webview`
4. Do NOT initialize with README (already have one)
5. Click **Create repository**

### Step 2 — Upload this project to GitHub
On your PC, open Command Prompt in the FlexRouteWebView folder and run:

```
git init
git add .
git commit -m "FlexRoute WebView wrapper"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/flexroute-webview.git
git push -u origin main
```

Replace YOUR_USERNAME with your GitHub username.

### Step 3 — Add secrets to GitHub
Go to your repo → Settings → Secrets and variables → Actions → New repository secret

Add these 4 secrets:

| Secret name       | Value                                              |
|-------------------|----------------------------------------------------|
| KEYSTORE_BASE64   | (see below — the base64 string of your keystore)   |
| KEYSTORE_PASSWORD | n62xEuYjsnC_                                       |
| KEY_ALIAS         | my-key-alias                                       |
| KEY_PASSWORD      | n62xEuYjsnC_                                       |

**KEYSTORE_BASE64 value** — copy this entire string:
MIIKwAIBAzCCCmoGCSqGSIb3DQEHAaCCClsEggpXMIIKUzCCBboGCSqGSIb3DQEHAaCCBasEggWnMIIFozCCBZ8GCyqGSIb3DQEMCgECoIIFQDCCBTwwZgYJKoZIhvcNAQUNMFkwOAYJKoZIhvcNAQUMMCsEFEogUzlfklzUy3X2yiwyrZg8tKqhAgInEAIBIDAMBggqhkiG9w0CCQUAMB0GCWCGSAFlAwQBKgQQ667x8ekGf264CB3pIzYEUwSCBNBvaMi8FabCfFsqtolDlckCwlWeHeJ7nbGg6u96iyMImGG++/LkkTpevcxCO//MONLE7DUxls+q/474bsyewzAvtWQAo/ZDz91ZxCK8GMWkLQW9z7iFyb/OAHwK+Jm8pczHYQDoRzvcd2xqp23RJGbU+YYk2WaEuw2L1Xs2NM/IJDafrFChafpRf+Gn71Ci8BNwu0xh7Ls13fkAJ8TsMO39UX+Zwc8l1m6GIFQ/s3WrkEFzJtSwEUUYaohTq3n9nPn9OkGpYn1R7P732qzSMVhCrE1ABOgBVxZDW9zfzDWYZSRLHEPYg4bq4cJGjc5cfAdY8p+FelJD6QzxK2EoHVkcK2P4KogOZyxUc6sBTEWtxh2cwTjt4dRORHF/8PLuxhiypxgSaTvsBZINkQs0W0aeqi75JV4WIXi996YVZS8HclnVox/l88hEIR0ngr+tab5CogXFr6iy/2/pwQKuslmufriwqZYUx3D9HZ6VSawDbJ1Z/+1ZU41WYuR9r2alj7rw58J+OJJo/xwMInDFrlRXka/gQbM3Ky0hEmTYt9k+7mo0o97/a1coJDU6+HQv7Y9yHmTyEqhz9w0KwVMZQxxOokVv5uGgav+ceGopNiVqlCW0rIJJjZro19OkWnbw6WmHurxN19v6Y/w7L5CSBhLxbEvtsE2VnPIybWx8RnjfXo2bkEZA8ip+L2FrRzD+g7LrdvI/cSeDiJ7Lp9A8Kjz2yNj5hrhCFwxdgnrcvl5RAWDJoeFZLrkQMB1K3NIW6x0hL00B2oJfD3Fp64fLGoJpDuCdCq35vw08M8544MItjbc0iIx5ZkigaDfHMtMKT4c9l4d8UhFWJEtPH33VqogY2HcqO09QDumQD1yTFYcRh6zLpcncrBLFFkBxHhlA3QQpIs1sI0auQG4PrhsbGRJtfzj1nIWGMja9PiD+AJFMJQoz7Q1Ma3ZcLrGM/vAI2iVkMB8pL9MissQiGu5YXcYg5YU3dSmFhvJCIIFQGZSQAZRMsmL/GAMZHBXzui9wp8j9iwv6UHe2zmAunmyU4ooGp2y7sg9Sl+JdkQH+miXt7tz95aQZ5GjqrEA5oydjGDAiwQduzzxtapqJ3STwUJ49pQgBGnJt9wMMQqz8bixQX0mq4em/ikxOXKTyabuINJ9eFj/vXRkpfC+02Css0QrIgFVMbIoelmKwphPARgaqzFWMTUI43iLMPfjczs7TYuPOf/Cr7dbSYsgrDO9LraI0JalPuFdyVEv1uOF9X32CmEhYbQt0xQxgQOeGxHB8Cn1JEj1KqNrnq5WA/7P6a7Sj7WHSMd4a4xQeAbCPy4zx+RBJ8PxDOXfkbzq4zoIfxjOUaZY7CBIVQ1Z9rkYjVT51EOW1kZeV2ZdsKkvLrCg/BtMhNfemqF55NQgMXzElJsJil6ZxJU0GcAEzINoVxZWjXnqVr1otr8OdF/nSLSrSwDDixBg2ave/DYPeamri98nxHgfsTUa5i46+e+9GDXANeRWzufv9tlhWm6ECHl3GrqYGyReOG54kP2UZ/huTdyyzXw5wKunPRtYVAwN6IARE7/QJoLcDkS/oGupLUwPEa1/HO0CROrlZFj89xawMjGu4sXB3cuCX1J5LBnKT5L4bJWVVz/6dLvl7VAa41s4irDFMMCcGCSqGSIb3DQEJFDEaHhgAbQB5AC0AawBlAHkALQBhAGwAaQBhAHMwIQYJKoZIhvcNAQkVMRQEElRpbWUgMTc4MDY1NzM2NTg0NzCCBJEGCSqGSIb3DQEHBqCCBIIwggR+AgEAMIIEdwYJKoZIhvcNAQcBMGYGCSqGSIb3DQEFDTBZMDgGCSqGSIb3DQEFDDArBBTv4TvxFEiw2IuHCCGWehrhUj14eAICJxACASAwDAYIKoZIhvcNAgkFADAdBglghkgBZQMEASoEEP9l40BIZiAK89sPLybgS7+AggQATVvGHRw4EuLy8iMj/P+D4bnvDG20+jxvWKaQ6Z6UO+GnsHzjVjfv7zZHLWH4LfKW7dxItfNmIIjnixo3eTMIB6igtVnY2EttlAcxszP1NqOnXB0Y4pBJ2XhqXFHMaw3bWiETjeQrBmo7ibaz4LjcXS34Ag4Qd2OeTTJFJGql3LpltsPQBf0raD5giQA1Yn8x3CcYBEdR90HnE7VOBpuJ8ZXcimklZ9jw5j4J/eY8h01p2cnDiHD6bDbbi0lZl1iGT5B2KFMcNOkh92sdQwqipmFZ3NBLXEHSVvFQ5czxHYyLR8c1U0umDyJEhbmebvI0lzMODYgJZBbcnQ0ObOytG4/vBASfD2ohny7VyTB/vOpRlrlGkj4JRGu7T8Jk1zwZx8NF/sFvi/XKAx3dN2q7RJN6G4fDwR7lZX1fI19hm4erQV39H1N4TiWJeoBgzMwpaeP3ynaiJihqwO/7+l3FhAJyFaMJ4P3yBTs0uEThJJhq9SaRDOwDTgJ99hiUbVnb0qjKA9KPbXfOdSTaxbApyq7UFU8+0cO8HByvUZf5TX6al+1/Gf9mZFq5wA635XEd2lwv/E5AR11ABQqAwfZxf6X9cg3n1kuLxW7apjaAjqerdkbpxGLbFKwYLBZF8KRPNyqHlcaIWuRAGAQBYaAro8brSoi/Nc3/y6t+5GgtqLT4SIxfSRu75f0s2JxX1xKByLu7SqYuWCPFGocPogJ7s3/V+19RuuEakcvaKMwuZcpzglKMwixFUQ6jTlbltijKATaefheGJsLtMEHzHE7rgqNCkWhqrrLZxqEN1ZnbrbLAE1zejs8LDm16GyTSojdYpkuwI9er0/3iTQbezt5gD6n2LB8k/p24KLVEW51jHX17bZz6+iqRXkUygN/Cwi5qmUOtNJyL3CeWqLi2pGsOYk4BHIjtTgkDr8Illykv7VIhy/ohLIK5xDxnUU7s86JjFmCiBWYR3br41Bpku16YranN0OOJeYM5s/QY7EzvK8kyRKjxzP/zyrQMl24Q5/jFleQgNpWsFvdIhDBWtOjdoCJWZivQZgT7gz1oppVT1Wh/W0p/r3sY4QjbJBfaczScH14QLN2wK87tb84YZO55r6fGj2z9qoWV36YHjDb5vB3dSibK+nZ7HpCKptn9iWABGveOeBm8ZjQRsrKR70QoyES1MJ0pdnkbkX7UBRROrTwwGqHydiigI5uYP3+V5XR3PGxvmjfXyu4T2n+jgYfc8X//XFmyJT3AmXMy/RDpwr+nFdyvLmlkV5MbBE6ZjRV/Wmle38VqC3QiHBiaw0kE+wqcpktZMmDxSYoVsay97j5MsAcpQpJSmD+1D5x7XTVCqNuTjQ8HDpx686gUBoQHXzBNMDEwDQYJYIZIAWUDBAIBBQAEIIcIAdDjTsB+olpseSf8DDs6wIP2TtSAQflTXMtv2WDgBBT/M3ojhGjtu+HOQFxH53m87Q4USQICJxA=

### Step 4 — Wait for the build (~5 minutes)
1. Go to your repo → **Actions** tab
2. You'll see the workflow running
3. Click on it to watch progress
4. When it shows a green checkmark ✅, click it
5. Scroll down to **Artifacts**
6. Download **FlexRoute-WebView-release**
7. Unzip it — inside is `app-release.aab`

### Step 5 — Upload to Play Console
1. Play Console → Closed Testing → Alpha → Create new release
2. Upload `app-release.aab`
3. Release name: `1.0.1 WebView`
4. Release notes: `WebView wrapper for closed testing`
5. Save and roll out

### Step 6 — After production access approved
Upload original TWA (`FlexRoute.aab`) as a new release with versionCode 3.
Done — users get the TWA back automatically.

---

## After you're done — change your passwords
Once production access is approved, change your keystore passwords:

```
keytool -storepasswd -keystore signing.keystore
keytool -keypasswd -keystore signing.keystore -alias my-key-alias
```

Also delete the GitHub repo (it contains your keystore secrets).
