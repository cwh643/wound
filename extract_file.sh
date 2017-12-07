rm -rf update_file/*
file_list=$(git status | grep modified | awk '{print $2}')
for file in $file_list ;do
    echo $file
    file_dir=$(dirname $file)
    out_file_dir=update_file/$file_dir
    #echo $out_file_dir
    #continue
    mkdir -p $out_file_dir
    cp -f $file $out_file_dir/
    echo $out_file_dir/$(basename $file)
done

tar -zcvf update_file.tar.gz update_file
