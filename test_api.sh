#!/bin/bash
# ============================================
# Blog API 全接口测试脚本 v2
# 用法: bash test_api.sh [base_url]
# ============================================
BASE="${1:-http://localhost:8080}"
TMPDIR="/tmp/blog_api_test"
PASS=0; FAIL=0
TS=$(date +%s)
mkdir -p "$TMPDIR"

ok()   { echo "  [PASS] $1"; ((PASS++)); }
fail() { echo "  [FAIL] $1 => $2"; ((FAIL++)); }

do_get() {
    local desc="$1" url="$2" expect="$3"
    local resp=$(curl -s "$url")
    local is_err=$(echo "$resp" | grep -o '"code":-1')
    if [ "$expect" = "ok" ] && [ -z "$is_err" ]; then ok "$desc"
    elif [ "$expect" = "err" ] && [ -n "$is_err" ]; then ok "$desc"
    else fail "$desc" "$resp"; fi
}

do_post() {
    local desc="$1" url="$2" file="$3" expect="$4"
    local resp=$(curl -s -X POST "$url" -H "Content-Type: application/json" -d @"$file")
    local is_err=$(echo "$resp" | grep -o '"code":-1')
    if [ "$expect" = "ok" ] && [ -z "$is_err" ]; then ok "$desc"
    elif [ "$expect" = "err" ] && [ -n "$is_err" ]; then ok "$desc"
    else fail "$desc" "$resp"; fi
}

do_put() {
    local desc="$1" url="$2" file="$3" expect="$4"
    local resp=$(curl -s -X PUT "$url" -H "Content-Type: application/json" -d @"$file")
    local is_err=$(echo "$resp" | grep -o '"code":-1')
    if [ "$expect" = "ok" ] && [ -z "$is_err" ]; then ok "$desc"
    elif [ "$expect" = "err" ] && [ -n "$is_err" ]; then ok "$desc"
    else fail "$desc" "$resp"; fi
}

do_delete() {
    local desc="$1" url="$2"
    local resp=$(curl -s -X DELETE "$url")
    [ -z "$(echo "$resp" | grep -o '"code":-1')" ] && ok "$desc" || fail "$desc" "$resp"
}

echo "=========================================="
echo "  Blog API 全接口测试 v2"
echo "  Base URL: $BASE  时间戳: $TS"
echo "=========================================="

# ==================== 1. User ====================
echo ""
echo "===== 1. 用户模块 ====="

cat > "$TMPDIR/u1.json" << EOF
{"username":"admin${TS}","password":"admin123","nickname":"管理员","email":"admin${TS}@blog.local"}
EOF
do_post "创建用户-合法" "$BASE/api/user" "$TMPDIR/u1.json" ok

cat > "$TMPDIR/u_dup.json" << EOF
{"username":"admin${TS}","password":"admin123","nickname":"重复","email":"dup@blog.local"}
EOF
do_post "创建用户-重名(username唯一性)" "$BASE/api/user" "$TMPDIR/u_dup.json" err

cat > "$TMPDIR/u2.json" << EOF
{"username":"u${TS}2","password":"admin123"}
EOF
do_post "创建用户-最小字段" "$BASE/api/user" "$TMPDIR/u2.json" ok

cat > "$TMPDIR/u3.json" << 'EOF'
{"username":"","password":"admin123"}
EOF
do_post "创建用户-空用户名" "$BASE/api/user" "$TMPDIR/u3.json" err

cat > "$TMPDIR/u4.json" << 'EOF'
{"username":"a","password":"admin123"}
EOF
do_post "创建用户-用户名过短(<2)" "$BASE/api/user" "$TMPDIR/u4.json" err

printf '{"username":"utest%sf","password":"%s"}' "$TS" $(printf 'x%.0s' {1..130}) > "$TMPDIR/u5.json"
do_post "创建用户-密码过长(>128)" "$BASE/api/user" "$TMPDIR/u5.json" err

cat > "$TMPDIR/u6.json" << 'EOF'
{"username":"emailtest","password":"123456","email":"not-an-email"}
EOF
do_post "创建用户-邮箱格式错误" "$BASE/api/user" "$TMPDIR/u6.json" err

do_get "查询用户-ID=1" "$BASE/api/user/1" ok

cat > "$TMPDIR/u_page.json" << 'EOF'
{"pageNum":1,"pageSize":5}
EOF
do_post "分页查询用户" "$BASE/api/user/page" "$TMPDIR/u_page.json" ok

cat > "$TMPDIR/u_upd.json" << EOF
{"id":1,"username":"admin_v2${TS}","password":"newpwd123","nickname":"超级管理员","email":"new${TS}@blog.local"}
EOF
do_put "更新用户-合法" "$BASE/api/user" "$TMPDIR/u_upd.json" ok

cat > "$TMPDIR/u_upd_bad.json" << 'EOF'
{"id":1,"username":""}
EOF
do_put "更新用户-空用户名" "$BASE/api/user" "$TMPDIR/u_upd_bad.json" err

do_delete "删除用户-ID=2" "$BASE/api/user/2"

# ==================== 2. Category ====================
echo ""
echo "===== 2. 分类模块 ====="

cat > "$TMPDIR/c_art.json" << EOF
{"name":"后端开发${TS}","type":"ARTICLE","sortOrder":1}
EOF
do_post "创建分类-ARTICLE" "$BASE/api/category" "$TMPDIR/c_art.json" ok

cat > "$TMPDIR/c_proj.json" << EOF
{"name":"开源项目${TS}","type":"PROJECT","sortOrder":1}
EOF
do_post "创建分类-PROJECT" "$BASE/api/category" "$TMPDIR/c_proj.json" ok

cat > "$TMPDIR/c_dup.json" << EOF
{"name":"前端开发${TS}","type":"ARTICLE","sortOrder":1}
EOF
do_post "创建分类-另一ARTICLE" "$BASE/api/category" "$TMPDIR/c_dup.json" ok

do_post "创建分类-重名(同type)" "$BASE/api/category" "$TMPDIR/c_art.json" err

cat > "$TMPDIR/c_same_diff.json" << EOF
{"name":"后端开发${TS}","type":"PROJECT","sortOrder":1}
EOF
do_post "创建分类-同名不同type(合法)" "$BASE/api/category" "$TMPDIR/c_same_diff.json" ok

cat > "$TMPDIR/c_empty.json" << 'EOF'
{"name":"","type":"ARTICLE"}
EOF
do_post "创建分类-空名称" "$BASE/api/category" "$TMPDIR/c_empty.json" err

do_get "查询分类-ID=1" "$BASE/api/category/1" ok

cat > "$TMPDIR/c_page.json" << 'EOF'
{"pageNum":1,"pageSize":5}
EOF
do_post "分页查询分类" "$BASE/api/category/page" "$TMPDIR/c_page.json" ok

cat > "$TMPDIR/c_upd.json" << EOF
{"id":1,"name":"后端进阶${TS}","type":"ARTICLE","sortOrder":2}
EOF
do_put "更新分类-合法" "$BASE/api/category" "$TMPDIR/c_upd.json" ok

do_delete "删除分类-ID=3" "$BASE/api/category/3"

# ==================== 3. Tag ====================
echo ""
echo "===== 3. 标签模块 ====="

cat > "$TMPDIR/t1.json" << EOF
{"name":"SpringBoot${TS}"}
EOF
do_post "创建标签-合法" "$BASE/api/tag" "$TMPDIR/t1.json" ok

do_post "创建标签-重名(唯一性)" "$BASE/api/tag" "$TMPDIR/t1.json" err

cat > "$TMPDIR/t_cn.json" << EOF
{"name":"微服务${TS}"}
EOF
do_post "创建标签-中文" "$BASE/api/tag" "$TMPDIR/t_cn.json" ok

cat > "$TMPDIR/t_empty.json" << 'EOF'
{"name":""}
EOF
do_post "创建标签-空名称" "$BASE/api/tag" "$TMPDIR/t_empty.json" err

printf '{"name":"%s"}' $(printf 'x%.0s' {1..35}) > "$TMPDIR/t_long.json"
do_post "创建标签-名称过长(>32)" "$BASE/api/tag" "$TMPDIR/t_long.json" err

do_get "查询标签-ID=1" "$BASE/api/tag/1" ok

cat > "$TMPDIR/t_page.json" << 'EOF'
{"pageNum":1,"pageSize":5}
EOF
do_post "分页查询标签" "$BASE/api/tag/page" "$TMPDIR/t_page.json" ok

cat > "$TMPDIR/t_upd.json" << EOF
{"id":1,"name":"SpringBoot3_${TS}"}
EOF
do_put "更新标签-合法(改名)" "$BASE/api/tag" "$TMPDIR/t_upd.json" ok

do_delete "删除标签-ID=2" "$BASE/api/tag/2"

# ==================== 4. Article ====================
echo ""
echo "===== 4. 文章模块 ====="

cat > "$TMPDIR/a1.json" << EOF
{"title":"SpringBoot入门指南${TS}","summary":"适合新手的教程","content":"# SpringBoot入门\n\n正文...","categoryId":1,"isPublished":1,"tagIds":[1]}
EOF
do_post "创建文章-合法(含标签)" "$BASE/api/article" "$TMPDIR/a1.json" ok

cat > "$TMPDIR/a2.json" << EOF
{"title":"最简文章${TS}","content":"正文内容","categoryId":1}
EOF
do_post "创建文章-最小字段" "$BASE/api/article" "$TMPDIR/a2.json" ok

cat > "$TMPDIR/a_bad_cat.json" << EOF
{"title":"测试","content":"正文","categoryId":99999}
EOF
do_post "创建文章-分类不存在" "$BASE/api/article" "$TMPDIR/a_bad_cat.json" err

cat > "$TMPDIR/a_proj_cat.json" << EOF
{"title":"测试","content":"正文","categoryId":2}
EOF
do_post "创建文章-选了项目分类" "$BASE/api/article" "$TMPDIR/a_proj_cat.json" err

cat > "$TMPDIR/a_no_title.json" << 'EOF'
{"title":"","content":"正文","categoryId":1}
EOF
do_post "创建文章-空标题" "$BASE/api/article" "$TMPDIR/a_no_title.json" err

cat > "$TMPDIR/a_no_content.json" << 'EOF'
{"title":"只有标题","categoryId":1}
EOF
do_post "创建文章-缺内容" "$BASE/api/article" "$TMPDIR/a_no_content.json" err

cat > "$TMPDIR/a_no_cat.json" << 'EOF'
{"title":"缺分类","content":"正文"}
EOF
do_post "创建文章-缺分类" "$BASE/api/article" "$TMPDIR/a_no_cat.json" err

printf '{"title":"%s","content":"正文","categoryId":1}' $(printf 'x%.0s' {1..260}) > "$TMPDIR/a_long.json"
do_post "创建文章-标题过长(>256)" "$BASE/api/article" "$TMPDIR/a_long.json" err

do_get "查询文章-ID=1" "$BASE/api/article/1" ok

cat > "$TMPDIR/a_page.json" << 'EOF'
{"pageNum":1,"pageSize":5}
EOF
do_post "分页查询文章" "$BASE/api/article/page" "$TMPDIR/a_page.json" ok

cat > "$TMPDIR/a_upd.json" << EOF
{"id":1,"title":"SpringBoot进阶${TS}","content":"更新内容","categoryId":1,"isPublished":1,"tagIds":[1]}
EOF
do_put "更新文章-合法" "$BASE/api/article" "$TMPDIR/a_upd.json" ok

cat > "$TMPDIR/a_upd_bad.json" << 'EOF'
{"id":1,"title":"","content":"内容","categoryId":1}
EOF
do_put "更新文章-空标题" "$BASE/api/article" "$TMPDIR/a_upd_bad.json" err

do_delete "删除文章-ID=2" "$BASE/api/article/2"

# ==================== 5. Project ====================
echo ""
echo "===== 5. 项目模块 ====="

# 动态获取 PROJECT / ARTICLE 类型的分类 ID
PROJ_CAT_ID=$(curl -s -X POST "$BASE/api/category/page" -H "Content-Type: application/json" -d '{"pageNum":1,"pageSize":20}' | sed 's/},{/}\n{/g' | grep '"type":"PROJECT"' | sed 's/.*"id":\([0-9]*\).*/\1/' | head -1)
ART_CAT_ID=$(curl -s -X POST "$BASE/api/category/page" -H "Content-Type: application/json" -d '{"pageNum":1,"pageSize":20}' | sed 's/},{/}\n{/g' | grep '"type":"ARTICLE"' | sed 's/.*"id":\([0-9]*\).*/\1/' | head -1)

cat > "$TMPDIR/p1.json" << EOF
{"name":"个人博客${TS}","summary":"全栈项目","content":"SpringBoot+Next.js","categoryId":${PROJ_CAT_ID},"techStack":"[\"SpringBoot\",\"Next.js\"]","githubUrl":"https://github.com/me/blog","isPublished":1}
EOF
do_post "创建项目-合法" "$BASE/api/project" "$TMPDIR/p1.json" ok

cat > "$TMPDIR/p_bad_cat.json" << 'EOF'
{"name":"测试","categoryId":99999}
EOF
do_post "创建项目-分类不存在" "$BASE/api/project" "$TMPDIR/p_bad_cat.json" err

cat > "$TMPDIR/p_art_cat.json" << EOF
{"name":"测试","categoryId":${ART_CAT_ID}}
EOF
do_post "创建项目-选了文章分类" "$BASE/api/project" "$TMPDIR/p_art_cat.json" err

cat > "$TMPDIR/p_no_name.json" << EOF
{"name":"","categoryId":${PROJ_CAT_ID}}
EOF
do_post "创建项目-空名称" "$BASE/api/project" "$TMPDIR/p_no_name.json" err

cat > "$TMPDIR/p_no_cat.json" << 'EOF'
{"name":"无分类项目"}
EOF
do_post "创建项目-缺分类" "$BASE/api/project" "$TMPDIR/p_no_cat.json" err

# 获取刚创建的项目 ID
PROJ_ID=$(curl -s -X POST "$BASE/api/project/page" -H "Content-Type: application/json" -d '{"pageNum":1,"pageSize":1,"sortFields":[{"field":"createTime","direction":"DESC"}]}' | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
do_get "查询项目" "$BASE/api/project/${PROJ_ID}" ok

cat > "$TMPDIR/p_page.json" << 'EOF'
{"pageNum":1,"pageSize":5}
EOF
do_post "分页查询项目" "$BASE/api/project/page" "$TMPDIR/p_page.json" ok

cat > "$TMPDIR/p_upd.json" << EOF
{"id":${PROJ_ID},"name":"BlogV2_${TS}","summary":"升级版","categoryId":${PROJ_CAT_ID},"isPublished":1}
EOF
do_put "更新项目-合法" "$BASE/api/project" "$TMPDIR/p_upd.json" ok

do_delete "删除项目" "$BASE/api/project/${PROJ_ID}"

# ==================== 6. Timeline ====================
echo ""
echo "===== 6. 时间线模块 ====="

cat > "$TMPDIR/tl1.json" << EOF
{"title":"开始学习Java${TS}","description":"第一本书","eventDate":"2023-03-15","sortOrder":1}
EOF
do_post "创建时间线-合法" "$BASE/api/timeline" "$TMPDIR/tl1.json" ok

cat > "$TMPDIR/tl_no_title.json" << 'EOF'
{"title":"","eventDate":"2023-03-15"}
EOF
do_post "创建时间线-空标题" "$BASE/api/timeline" "$TMPDIR/tl_no_title.json" err

cat > "$TMPDIR/tl_no_date.json" << 'EOF'
{"title":"缺日期"}
EOF
do_post "创建时间线-缺日期" "$BASE/api/timeline" "$TMPDIR/tl_no_date.json" err

do_get "查询时间线-ID=1" "$BASE/api/timeline/1" ok

cat > "$TMPDIR/tl_page.json" << 'EOF'
{"pageNum":1,"pageSize":5}
EOF
do_post "分页查询时间线" "$BASE/api/timeline/page" "$TMPDIR/tl_page.json" ok

cat > "$TMPDIR/tl_upd.json" << EOF
{"id":1,"title":"Java进阶${TS}","description":"深入研究JVM","eventDate":"2023-06-01","sortOrder":1}
EOF
do_put "更新时间线-合法" "$BASE/api/timeline" "$TMPDIR/tl_upd.json" ok

do_delete "删除时间线-ID=2" "$BASE/api/timeline/2"

# ==================== 7. Skill ====================
echo ""
echo "===== 7. 技能模块 ====="

cat > "$TMPDIR/s1.json" << EOF
{"name":"Java${TS}","category":"后端","proficiency":90,"sortOrder":1}
EOF
do_post "创建技能-合法" "$BASE/api/skill" "$TMPDIR/s1.json" ok

do_post "创建技能-重名(唯一性)" "$BASE/api/skill" "$TMPDIR/s1.json" err

cat > "$TMPDIR/s_no_name.json" << 'EOF'
{"name":"","proficiency":50}
EOF
do_post "创建技能-空名称" "$BASE/api/skill" "$TMPDIR/s_no_name.json" err

cat > "$TMPDIR/s_min.json" << 'EOF'
{"name":"测试","proficiency":-1}
EOF
do_post "创建技能-熟练度<0" "$BASE/api/skill" "$TMPDIR/s_min.json" err

cat > "$TMPDIR/s_max.json" << 'EOF'
{"name":"测试","proficiency":101}
EOF
do_post "创建技能-熟练度>100" "$BASE/api/skill" "$TMPDIR/s_max.json" err

do_get "查询技能-ID=1" "$BASE/api/skill/1" ok

cat > "$TMPDIR/s_page.json" << 'EOF'
{"pageNum":1,"pageSize":5,"sortFields":[{"field":"proficiency","direction":"DESC"}]}
EOF
do_post "分页查询技能" "$BASE/api/skill/page" "$TMPDIR/s_page.json" ok

cat > "$TMPDIR/s_upd.json" << EOF
{"id":1,"name":"JavaExpert${TS}","category":"后端","proficiency":95,"sortOrder":1}
EOF
do_put "更新技能-合法(改名)" "$BASE/api/skill" "$TMPDIR/s_upd.json" ok

do_delete "删除技能-ID=2" "$BASE/api/skill/2"

# ==================== 8. About ====================
echo ""
echo "===== 8. 关于模块 ====="

do_get "查询关于信息" "$BASE/api/about" ok

cat > "$TMPDIR/about1.json" << 'EOF'
{"content":"一名后端开发工程师。","contactEmail":"me@example.com","githubUrl":"https://github.com/me"}
EOF
do_put "更新关于-合法" "$BASE/api/about" "$TMPDIR/about1.json" ok

cat > "$TMPDIR/about_bad.json" << 'EOF'
{"content":"介绍","contactEmail":"invalid-email"}
EOF
do_put "更新关于-邮箱格式错误" "$BASE/api/about" "$TMPDIR/about_bad.json" err

# ==================== 9. Comment ====================
echo ""
echo "===== 9. 评论模块 ====="

cat > "$TMPDIR/cm1.json" << EOF
{"articleId":1,"authorName":"访客张三${TS}","authorEmail":"zs${TS}@example.com","content":"写得很好!"}
EOF
do_post "创建评论-合法" "$BASE/api/comment" "$TMPDIR/cm1.json" ok

cat > "$TMPDIR/cm2.json" << EOF
{"articleId":1,"authorName":"匿名${TS}","content":"支持一下"}
EOF
do_post "创建评论-最小字段" "$BASE/api/comment" "$TMPDIR/cm2.json" ok

cat > "$TMPDIR/cm_no_art.json" << 'EOF'
{"authorName":"用户","content":"内容"}
EOF
do_post "创建评论-缺文章ID" "$BASE/api/comment" "$TMPDIR/cm_no_art.json" err

cat > "$TMPDIR/cm_bad_art.json" << 'EOF'
{"articleId":99999,"authorName":"用户","content":"内容"}
EOF
do_post "创建评论-文章不存在" "$BASE/api/comment" "$TMPDIR/cm_bad_art.json" err

cat > "$TMPDIR/cm_no_name.json" << 'EOF'
{"articleId":1,"authorName":"","content":"内容"}
EOF
do_post "创建评论-空昵称" "$BASE/api/comment" "$TMPDIR/cm_no_name.json" err

cat > "$TMPDIR/cm_no_content.json" << 'EOF'
{"articleId":1,"authorName":"用户"}
EOF
do_post "创建评论-缺内容" "$BASE/api/comment" "$TMPDIR/cm_no_content.json" err

cat > "$TMPDIR/cm_bad_email.json" << 'EOF'
{"articleId":1,"authorName":"用户","content":"内容","authorEmail":"bad-email"}
EOF
do_post "创建评论-邮箱格式错误" "$BASE/api/comment" "$TMPDIR/cm_bad_email.json" err

do_get "查询评论-ID=1" "$BASE/api/comment/1" ok

cat > "$TMPDIR/cm_page.json" << 'EOF'
{"pageNum":1,"pageSize":5}
EOF
do_post "分页查询评论" "$BASE/api/comment/page" "$TMPDIR/cm_page.json" ok

cat > "$TMPDIR/cm_upd.json" << 'EOF'
{"id":1,"articleId":1,"authorName":"访客张三","content":"更新后的评论内容"}
EOF
do_put "更新评论-合法" "$BASE/api/comment" "$TMPDIR/cm_upd.json" ok

do_delete "删除评论-ID=2" "$BASE/api/comment/2"

# ==================== 10. Media ====================
echo ""
echo "===== 10. 媒体模块 ====="

cat > "$TMPDIR/m1.json" << EOF
{"filename":"cover${TS}.png","originalFilename":"cover.png","filePath":"/blog/images/cover${TS}.png","fileUrl":"http://localhost:19090/blog/images/cover${TS}.png","fileSize":204800,"mimeType":"image/png"}
EOF
do_post "创建媒体-合法" "$BASE/api/media" "$TMPDIR/m1.json" ok

cat > "$TMPDIR/m_no_name.json" << 'EOF'
{"filename":"","fileUrl":"http://example.com/file.png","fileSize":100,"mimeType":"image/png"}
EOF
do_post "创建媒体-空文件名" "$BASE/api/media" "$TMPDIR/m_no_name.json" err

cat > "$TMPDIR/m_no_url.json" << 'EOF'
{"filename":"file.png","fileUrl":"","fileSize":100,"mimeType":"image/png"}
EOF
do_post "创建媒体-空URL" "$BASE/api/media" "$TMPDIR/m_no_url.json" err

cat > "$TMPDIR/m_neg.json" << 'EOF'
{"filename":"file.png","fileUrl":"http://example.com/file.png","fileSize":-1,"mimeType":"image/png"}
EOF
do_post "创建媒体-文件大小为负" "$BASE/api/media" "$TMPDIR/m_neg.json" err

do_get "查询媒体-ID=1" "$BASE/api/media/1" ok

cat > "$TMPDIR/m_page.json" << 'EOF'
{"pageNum":1,"pageSize":5}
EOF
do_post "分页查询媒体" "$BASE/api/media/page" "$TMPDIR/m_page.json" ok

cat > "$TMPDIR/m_upd.json" << EOF
{"id":1,"filename":"new${TS}.png","fileUrl":"http://localhost:19090/blog/images/new${TS}.png","fileSize":300000,"mimeType":"image/png"}
EOF
do_put "更新媒体-合法" "$BASE/api/media" "$TMPDIR/m_upd.json" ok

do_delete "删除媒体-ID=2" "$BASE/api/media/2"

# ==================== 统计 ====================
rm -rf "$TMPDIR"
echo ""
echo "=========================================="
echo "  测试完成: 通过=$PASS  失败=$FAIL  总计=$((PASS+FAIL))"
echo "=========================================="
[ "$FAIL" -eq 0 ] && echo "  全部通过!" || echo "  有 $FAIL 个用例失败!"
