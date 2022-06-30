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
        <?php $temp = 24;
            $light = 24;
            $modality = 24;
            var_dump("hey".$_GET["temp"]);
            echo "ciaoooooo".$_POST["temp"];
            var_dump($_POST["temp"]);
        if(isset($_POST["temp"]) && isset($_POST["light"]) && isset($_POST["modality"]) && isset($_POST["alarm"])): 
            $temp = $_POST["temp"];
            $light = $_POST["light"];
            $modality = $_POST["modality"];
            $alarm = $_POST["alarm"];
            echo "tempppp".$temp; endif ?>
            <section>
                <h1>Garden Dashboard</h1>
                <table>
                    <tr>
                      <th>Temperature</th>
                      <th>Lightness value</th>
                      <th>Modality</th>
                    </tr>
                    <tr>
                      <td><?php echo $temp; ?></td>
                      <td><?php echo $light; ?></td>
                      <td><?php echo $modality; ?></td>
                    </tr>
                  </table>
            </section>
                        
        </main>
        <!--footer-->
        <footer>
           
        </footer>
    </body>
</html>