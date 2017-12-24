$Filename=$args[0];
$savingFilename=$args[1];
$Word=NEW-Object -ComObject Word.Application;
$Document=$Word.Documents.Open($Filename);
$Document.SaveAs([REF] $savingFilename);
$Document.Close();
$Word.Quit();
$Word = $Null;