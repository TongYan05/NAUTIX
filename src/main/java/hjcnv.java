public class hjcnv {
    public static void main(String[] args) {
        int total  = 0;//expression
        int i;//statement
        for(i=0;i<=5;i++);//the semicolon is missing/existing
        {
            total += i;
        }
        System.out.println(total);//statement
    }
}
//expression has value but statement do not have value
//  /list  /vars  /exit  /history   /method  /save mysnippets.jsh  /jshell --starup mysnippets.jsh  /edit
//local version control system-vcs    -> use database to track all the changed files