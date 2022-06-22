<!DOCTYPE html>
<html lang="it">
    <head>
        <title>Garden Dashboard</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" type="text/css" href="./css/style.css">
        <script
		src="https://code.jquery.com/jquery-3.4.1.min.js"
		integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
		crossorigin="anonymous" defer="defer"></script>
    </head>
    <body>
        <!--header-->
        <header >
        </header>
        <!--main-->
        <main>
            <section>
                <h1>Garden Dashboard</h1>
                <table>
                    <tr>
                      <th>Temperature</th>
                      <th>Lightness value</th>
                      <th>Modality</th>
                    </tr>
                    <tr>
                      <td>24*C</td>
                      <td>2</td>
                      <td>ALLARM</td>
                    </tr>
                  </table>
            </section>
            <section>
              <main>
              <?php if(isset($_GET["error"])):  // $_SESSION["isonline"] = 1; header("Location:./home-".$_SESSION["account"].".php");?>
                <div class="text-center panel panel-danger">
                    <div class="panel-heading">Errore</div>
                    <div class="panel-body"><?php echo $_GET["error"]; ?> </div>
                </div>
            <?php endif; ?>
            <form name="accesso" action="template/controllo_accesso.php" method="POST">

                <div class="container-login">
                    <fieldset >
                        <legend>Accedi Come:</legend>
                        <div>
                            <input type="radio" id="utente" name="account" value="Utente" required>
                            <label for="utente">Utente</label>
                        </div>
                        <div>
                            <input type="radio" id="venditore" name="account" value="Venditore" required>
                            <label for="venditore">Venditore</label>
                        </div>
                        <div>
                            <input type="radio" id="fattorino" name="account" value="Fattorino" required>
                            <label for="fattorino">Fattorino</label>
                        </div>
                    </fieldset>

                    <label for="user">Username:</label>
                    <input type="text" id="user" name="user" required>

                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <input type="submit" value="Accedi">
            </form>
            </main>
            </section>
            
        </main>
        <!--footer-->
        <footer>
           
        </footer>
    </body>
</html>

