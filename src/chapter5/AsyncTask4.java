package chapter5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import utils.PrintUtil;

/**
 * 在一个文件夹以及其子文件夹中查找制定扩展名的文件
 */
public class AsyncTask4 {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FolderProcessor task = new FolderProcessor("/Volumes/macData/asdad", "dmg");
        forkJoinPool.invoke(task);
        forkJoinPool.shutdown();
        List<String> result = task.get();
        for (String file : result) {
            PrintUtil.println(file);
        }
    }
}

class FolderProcessor extends RecursiveTask<List<String>> {

    private String path;
    private String extension;

    FolderProcessor(String path, String extension) {
        this.path = path;
        this.extension = extension;
    }

    @Override
    protected List<String> compute() {
        List<String> list = new ArrayList<>();
        List<FolderProcessor> tasks = new ArrayList<>();
        File file = new File(path);
        File content[] = file.listFiles();
        if (content != null) {
            for (File f : content) {
                try {
                    if (!f.getCanonicalFile().equals(f.getAbsoluteFile()))
                        break;
                    if (f.isDirectory()) {
                        FolderProcessor task = new FolderProcessor(f.getAbsolutePath(), extension);
                        task.fork();
                        tasks.add(task);
                    } else {
                        if (f.getName().endsWith(extension)) {
                            list.add(f.getAbsolutePath());
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        if (tasks.size() > 50) {
            PrintUtil.printf("路径 %s 运行了 %d 个任务", path, tasks.size());
        }
        addResultFromTasks(list, tasks);
        //PrintUtil.println("return " + path);
        return list;
    }

    private void addResultFromTasks(List<String> list, List<FolderProcessor> tasks) {
        for (FolderProcessor task : tasks) {
            // 调用 join() 当前线程挂起,进入同步模式,ForkJoinPool 可以使用工作窃取算法
            // 与 invoke() 不同的地方:大致就是同步的位置不同.invoke() 调用时马上同步.而 fork() 调用后,在 join() 后者 get() 的地方同步
            list.addAll(task.join());
//            try {
//                list.addAll(task.get());
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
        }
    }

}
//        output:
//
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources 运行了 54 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules 运行了 251 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules 运行了 572 个任务
//        路径 /Volumes/macData/asdad/Android Studio 3.0 Preview.app/Contents/plugins/android/lib/layoutlib/data/res 运行了 1532 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules/git-utils/deps/libgit2/tests/resources 运行了 53 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules/git-utils/deps/libgit2/tests/resources/cherrypick/.gitted/objects 运行了 66 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules/git-utils/deps/libgit2/tests/resources/crlf/.gitted/objects 运行了 52 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules/git-utils/deps/libgit2/tests/resources/diff_format_email/.gitted/objects 运行了 65 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules/git-utils/deps/libgit2/tests/resources/merge-recursive/.gitted/objects 运行了 100 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules/git-utils/deps/libgit2/tests/resources/nasty/.gitted/objects 运行了 89 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules/git-utils/deps/libgit2/tests/resources/testrepo/.gitted/objects 运行了 54 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules/git-utils/deps/libgit2/tests/resources 运行了 53 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules/git-utils/deps/libgit2/tests/resources/crlf/.gitted/objects 运行了 52 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules/git-utils/deps/libgit2/tests/resources/cherrypick/.gitted/objects 运行了 66 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules/git-utils/deps/libgit2/tests/resources/diff_format_email/.gitted/objects 运行了 65 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules/git-utils/deps/libgit2/tests/resources/nasty/.gitted/objects 运行了 89 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules/git-utils/deps/libgit2/tests/resources/rebase/.gitted/objects 运行了 93 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules/git-utils/deps/libgit2/tests/resources/testrepo/.gitted/objects 运行了 54 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules/git-utils/deps/libgit2/tests/resources/merge-recursive/.gitted/objects 运行了 100 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules/git-utils/deps/libgit2/tests/resources/rebase/.gitted/objects 运行了 93 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/node_modules/git-utils/deps/libgit2/tests/resources/merge-resolve/.gitted/objects 运行了 192 个任务
//        路径 /Volumes/macData/asdad/Atom.app/Contents/Resources/app/apm/node_modules/git-utils/deps/libgit2/tests/resources/merge-resolve/.gitted/objects 运行了 192 个任务
//        /Volumes/macData/asdad/aria2-1.32.0-osx-darwin-build2.dmg
//        /Volumes/macData/asdad/BaiduNetdisk_mac_2.2.0.dmg
//        /Volumes/macData/asdad/CocosCreator_v1.5.2_2017070701.dmg
//        /Volumes/macData/asdad/datagrip-2017.2.dmg
//        /Volumes/macData/asdad/DropboxInstaller.dmg
//        /Volumes/macData/asdad/EUI_MAC.dmg
//        /Volumes/macData/asdad/ideaIU-2017.2.dmg
//        /Volumes/macData/asdad/NeteaseMusic_1.5.6_566_web.dmg
//        /Volumes/macData/asdad/pycharm-professional-2017.2.dmg
//        /Volumes/macData/asdad/sogou_mac_43a.dmg
//        /Volumes/macData/asdad/steam.dmg
//        /Volumes/macData/asdad/wechat_web_devtools_0.20.191900.dmg
//        /Volumes/macData/asdad/Xone-Driver-1.0.4.dmg