<html>
    
    <head>
        <title>Quiip</title>
        <meta charset="utf-8"/>
    </head>
    
    <body>
        
        <form action ="<?php echo $_SERVER['PHP_SELF'];?>" method="post">
            
            <table border="0">
                
                <tr>
                    
                    <td>Correo: </td>
                    <td><input type="text" name="correo" value="<?php if ( isset( $_POST['correo'] ) ) echo $_POST['correo'];?>"/></td>
                    <td><input type="submit" name="submit" value"Enviar"/></td>
                </tr>
                
            </table>
            
        </form>
        
    </body>
</html>


<?php

    require_once('connectvars.php');
    
    if ( isset( $_POST['submit'] ) ) {
        
        $correo = $_POST['correo'];
        
        if ( empty($correo) ) {
            
            echo '<p>Debe de introducir un correo </p>';
        }
        elseif (!filter_var($correo, FILTER_VALIDATE_EMAIL)) {
            
            echo '<p>Debe de introducir un formato de correo correcto</p>';
        }
        else 
        {
            //Comprobamos que el correo exista    
            
            $conn   = mysqli_connect( SERVER, USER, PASS, BD );
            
            $query  = "select id from usuarios where email = '$correo'";
            $res    = mysqli_query( $conn, $query );
            
            if ( mysqli_num_rows($res) == 0 ) {
                
                echo '<p>Correo no registrado</p>';
            } 
            else 
            {
                session_start();
                $_SESSION['correo'] = mysqli_fetch_array($res)[0];
                header('Location: notasuser.php');
            }
        }
        
    }


?>