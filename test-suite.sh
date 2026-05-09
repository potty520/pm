#!/usr/bin/env bash
# ============================================================
# Auction System - Full Functional Test Suite
# Usage: bash test-suite.sh
# ============================================================
set -uo pipefail

BASE="http://localhost:8080/api"
PASS=0
FAIL=0
TS=$(date +%s)
unset http_proxy https_proxy HTTP_PROXY HTTPS_PROXY

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m'

SELLER_USER="seller${TS}"
BUYER_USER="buyer${TS}"
SELLER_PW="seller123"
BUYER_PW="buyer123"
ADMIN_PW="admin123"

assert_eq() {
    local desc="$1" expected="$2" actual="$3"
    if [ "$actual" = "$expected" ]; then
        echo -e "  ${GREEN}PASS${NC} $desc"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}FAIL${NC} $desc (expected: $expected, got: $actual)"
        FAIL=$((FAIL + 1))
    fi
}

assert_ok() {
    local desc="$1" code="$2"
    if [ "$code" = "200" ]; then
        echo -e "  ${GREEN}PASS${NC} $desc"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}FAIL${NC} $desc (code=$code)"
        FAIL=$((FAIL + 1))
    fi
}

assert_rejected() {
    local desc="$1" code="$2"
    if [ "$code" != "200" ]; then
        echo -e "  ${GREEN}PASS${NC} $desc (code=$code)"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}FAIL${NC} $desc (should be rejected)"
        FAIL=$((FAIL + 1))
    fi
}

api() {
    local method="$1" url="$2" token="$3" data="$4"
    local -a args=(-s -X "$method" "$BASE$url" -H "Content-Type: application/json")
    [ -n "$token" ] && args+=(-H "Authorization: Bearer $token")
    [ -n "$data" ] && args+=(-d "$data")
    curl "${args[@]}"
}

get_code() {
    echo "$1" | python3 -c "import sys,json; print(json.load(sys.stdin)['code'])" 2>/dev/null || echo "no_json"
}
get_field() {
    echo "$1" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['$2'])" 2>/dev/null || echo ""
}
list_field() {
    # extract field from first element of data array
    echo "$1" | python3 -c "import sys,json; d=json.load(sys.stdin)['data']; print(d[0]['$2'] if d else '')" 2>/dev/null || echo ""
}

echo "============================================================"
echo "  Auction System Functional Test Suite"
echo "  Date: $(date)"
echo "  Users: $SELLER_USER, $BUYER_USER"
echo "============================================================"

# ============================================================
echo -e "\n${YELLOW}[Phase 1] Authentication & Registration${NC}"

echo "--- 1.1 Register seller ---"
RESP=$(api POST /user/register "" "{\"username\":\"$SELLER_USER\",\"password\":\"$SELLER_PW\",\"nickname\":\"Seller Wang\",\"phone\":\"13900139000\",\"email\":\"seller@test.com\"}")
assert_ok "Register seller" "$(get_code "$RESP")"
SELLER_ID=$(get_field "$RESP" "id")
echo "    Seller ID: $SELLER_ID"

echo "--- 1.2 Duplicate username rejected ---"
RESP=$(api POST /user/register "" "{\"username\":\"$SELLER_USER\",\"password\":\"xxx123456\",\"nickname\":\"X\"}")
assert_rejected "Duplicate rejected" "$(get_code "$RESP")"

echo "--- 1.3 Short password rejected ---"
RESP=$(api POST /user/register "" "{\"username\":\"u${TS}\",\"password\":\"12\",\"nickname\":\"X\"}")
assert_rejected "Short password" "$(get_code "$RESP")"

echo "--- 1.4 Login seller ---"
RESP=$(api POST /user/login "" "{\"username\":\"$SELLER_USER\",\"password\":\"$SELLER_PW\"}")
assert_ok "Login seller" "$(get_code "$RESP")"
SELLER_TOKEN=$(get_field "$RESP" "token")

echo "--- 1.5 Wrong password rejected ---"
RESP=$(api POST /user/login "" "{\"username\":\"$SELLER_USER\",\"password\":\"wrongpass\"}")
assert_rejected "Wrong password" "$(get_code "$RESP")"

echo "--- 1.6 Get own profile ---"
RESP=$(api GET /user/me "$SELLER_TOKEN" "")
assert_ok "Get profile" "$(get_code "$RESP")"

echo "--- 1.7 Update profile ---"
RESP=$(api PUT /user/me "$SELLER_TOKEN" "{\"nickname\":\"Seller Wang Pro\"}")
assert_ok "Update profile" "$(get_code "$RESP")"

# ============================================================
echo -e "\n${YELLOW}[Phase 2] Admin & System Setup${NC}"

echo "--- 2.1 Admin login ---"
RESP=$(api POST /user/login "" "{\"username\":\"admin\",\"password\":\"$ADMIN_PW\"}")
assert_ok "Admin login" "$(get_code "$RESP")"
ADMIN_TOKEN=$(get_field "$RESP" "token")

echo "--- 2.2 Create category ---"
RESP=$(api POST /admin/categories "$ADMIN_TOKEN" '{"name":"Antiques","parentId":0,"sort":1}')
assert_ok "Create category" "$(get_code "$RESP")"
CAT_ID=$(get_field "$RESP" "id")

echo "--- 2.3 Create second category ---"
RESP=$(api POST /admin/categories "$ADMIN_TOKEN" '{"name":"Jewelry","parentId":0,"sort":2}')
assert_ok "Create category 2" "$(get_code "$RESP")"

echo "--- 2.4 List categories ---"
RESP=$(api GET /category/list "" "")
assert_ok "List categories" "$(get_code "$RESP")"

echo "--- 2.5 Update category ---"
RESP=$(api PUT /admin/categories "$ADMIN_TOKEN" "{\"id\":$CAT_ID,\"name\":\"Antiques & Art\",\"parentId\":0,\"sort\":1}")
assert_ok "Update category" "$(get_code "$RESP")"

echo "--- 2.6 Create auction session ---"
RESP=$(api POST /admin/sessions "$ADMIN_TOKEN" '{"name":"Spring Auction 2026","startTime":"2026-05-10T09:00:00","endTime":"2026-05-20T18:00:00"}')
assert_ok "Create session" "$(get_code "$RESP")"
SESS_ID=$(get_field "$RESP" "id")

echo "--- 2.7 List sessions ---"
RESP=$(api GET /auction/session/list "" "")
assert_ok "List sessions" "$(get_code "$RESP")"

echo "--- 2.8 Admin overview stats ---"
RESP=$(api GET /admin/stats/overview "$ADMIN_TOKEN" "")
assert_ok "Stats" "$(get_code "$RESP")"

echo "--- 2.9 Non-admin blocked from admin API ---"
RESP=$(api GET /admin/stats/overview "$SELLER_TOKEN" "")
assert_rejected "Non-admin blocked" "$(get_code "$RESP")"

echo "--- 2.10 Unauthenticated access returns 403 ---"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/user/me")
assert_eq "403 on no auth" "403" "$HTTP_CODE"

# ============================================================
echo -e "\n${YELLOW}[Phase 3] Item Publishing${NC}"

echo "--- 3.1 Publish without login rejected ---"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/auction/publish" -H "Content-Type: application/json" -d '{"title":"X"}')
assert_eq "Publish w/o login=403" "403" "$HTTP_CODE"

echo "--- 3.2 Publish item ---"
RESP=$(api POST /auction/publish "$SELLER_TOKEN" "{\"title\":\"Ming Dynasty Vase\",\"description\":\"Beautiful Ming dynasty vase\",\"categoryId\":$CAT_ID,\"sessionId\":$SESS_ID,\"startPrice\":1000,\"reservePrice\":5000,\"bidIncrement\":200}")
assert_ok "Publish item" "$(get_code "$RESP")"
ITEM_ID=$(get_field "$RESP" "id")
assert_eq "Status pending(0)" "0" "$(get_field "$RESP" "status")"

echo "--- 3.3 Publish second item ---"
RESP=$(api POST /auction/publish "$SELLER_TOKEN" "{\"title\":\"Jade Bracelet\",\"description\":\"Ancient jade bracelet\",\"categoryId\":$CAT_ID,\"sessionId\":$SESS_ID,\"startPrice\":2000,\"reservePrice\":8000,\"bidIncrement\":500}")
assert_ok "Publish item 2" "$(get_code "$RESP")"
ITEM2_ID=$(get_field "$RESP" "id")

echo "--- 3.4 View item detail ---"
RESP=$(api GET "/auction/item/$ITEM_ID" "" "")
assert_ok "Item detail" "$(get_code "$RESP")"

echo "--- 3.5 Search by keyword ---"
RESP=$(api GET "/auction/search?keyword=Ming" "" "")
assert_ok "Search keyword" "$(get_code "$RESP")"

echo "--- 3.6 Search by category ---"
RESP=$(api GET "/auction/search?categoryId=$CAT_ID" "" "")
assert_ok "Search category" "$(get_code "$RESP")"

echo "--- 3.7 List active items (empty until approved) ---"
RESP=$(api GET /auction/list "" "")
assert_ok "List active" "$(get_code "$RESP")"

# ============================================================
echo -e "\n${YELLOW}[Phase 4] Admin Review${NC}"

echo "--- 4.1 Non-admin cannot review ---"
RESP=$(api POST "/auction/review/$ITEM_ID" "$SELLER_TOKEN" '{"approved":true}')
assert_rejected "Non-admin review" "$(get_code "$RESP")"

echo "--- 4.2 Admin approve item ---"
RESP=$(api POST "/auction/review/$ITEM_ID" "$ADMIN_TOKEN" '{"approved":true}')
assert_ok "Approve item" "$(get_code "$RESP")"
assert_eq "Status approved(1)" "1" "$(get_field "$RESP" "status")"

echo "--- 4.3 Admin reject item 2 ---"
RESP=$(api POST "/auction/review/$ITEM2_ID" "$ADMIN_TOKEN" '{"approved":false,"rejectReason":"Insufficient description"}')
assert_ok "Reject item" "$(get_code "$RESP")"
assert_eq "Status rejected(2)" "2" "$(get_field "$RESP" "status")"

# ============================================================
echo -e "\n${YELLOW}[Phase 5] Bidding${NC}"

echo "--- 5.1 Register buyer ---"
RESP=$(api POST /user/register "" "{\"username\":\"$BUYER_USER\",\"password\":\"$BUYER_PW\",\"nickname\":\"Buyer Li\"}")
assert_ok "Register buyer" "$(get_code "$RESP")"
BUYER_ID=$(get_field "$RESP" "id")

echo "--- 5.2 Buyer login ---"
RESP=$(api POST /user/login "" "{\"username\":\"$BUYER_USER\",\"password\":\"$BUYER_PW\"}")
assert_ok "Buyer login" "$(get_code "$RESP")"
BUYER_TOKEN=$(get_field "$RESP" "token")

echo "--- 5.3 Add deposit ---"
RESP=$(api POST /user/deposit "$BUYER_TOKEN" '{"amount":10000}')
assert_ok "Add deposit" "$(get_code "$RESP")"

echo "--- 5.4 Activate item for auction (set status=3) ---"
mysql -u root -proot auction_db -e "UPDATE auction_item SET status=3, start_time=NOW() WHERE id=$ITEM_ID" 2>/dev/null
RESP=$(api GET "/auction/item/$ITEM_ID" "" "")
assert_eq "Item active(3)" "3" "$(get_field "$RESP" "status")"

echo "--- 5.5 Cannot bid on rejected item ---"
RESP=$(api POST "/auction/bid/$ITEM2_ID" "$BUYER_TOKEN" "{\"userId\":$BUYER_ID,\"bidAmount\":3000}")
assert_rejected "Bid on rejected" "$(get_code "$RESP")"

echo "--- 5.6 Bid below minimum rejected ---"
RESP=$(api POST "/auction/bid/$ITEM_ID" "$BUYER_TOKEN" "{\"userId\":$BUYER_ID,\"bidAmount\":500}")
assert_rejected "Bid too low" "$(get_code "$RESP")"

echo "--- 5.7 Place first bid ---"
RESP=$(api POST "/auction/bid/$ITEM_ID" "$BUYER_TOKEN" "{\"userId\":$BUYER_ID,\"bidAmount\":1500}")
assert_ok "Bid 1500" "$(get_code "$RESP")"

echo "--- 5.8 Place higher bid ---"
RESP=$(api POST "/auction/bid/$ITEM_ID" "$BUYER_TOKEN" "{\"userId\":$BUYER_ID,\"bidAmount\":2500}")
assert_ok "Bid 2500" "$(get_code "$RESP")"

echo "--- 5.9 Place highest bid ---"
RESP=$(api POST "/auction/bid/$ITEM_ID" "$BUYER_TOKEN" "{\"userId\":$BUYER_ID,\"bidAmount\":4000}")
assert_ok "Bid 4000" "$(get_code "$RESP")"

echo "--- 5.10 Get bid records ---"
RESP=$(api GET "/auction/bids/$ITEM_ID" "" "")
assert_ok "Bid records" "$(get_code "$RESP")"

echo "--- 5.11 Current price updated ---"
RESP=$(api GET "/auction/item/$ITEM_ID" "" "")
assert_eq "Price=4000" "4000.0" "$(get_field "$RESP" "currentPrice")"

# ============================================================
echo -e "\n${YELLOW}[Phase 6] End Auction & Create Order${NC}"

echo "--- 6.1 Non-seller cannot end auction ---"
RESP=$(api POST "/auction/end/$ITEM_ID" "$BUYER_TOKEN" "")
assert_rejected "Non-seller end" "$(get_code "$RESP")"

echo "--- 6.2 Seller ends auction ---"
RESP=$(api POST "/auction/end/$ITEM_ID" "$SELLER_TOKEN" "")
assert_ok "Seller ends auction" "$(get_code "$RESP")"

echo "--- 6.3 Item marked as sold ---"
RESP=$(api GET "/auction/item/$ITEM_ID" "" "")
assert_eq "Status sold(4)" "4" "$(get_field "$RESP" "status")"

echo "--- 6.4 Order created ---"
RESP=$(api GET /order/my "$BUYER_TOKEN" "")
assert_ok "Buyer orders" "$(get_code "$RESP")"
ORDER_ID=$(list_field "$RESP" "id")
echo "    Order ID: $ORDER_ID"

echo "--- 6.5 Seller sees order ---"
RESP=$(api GET /order/my "$SELLER_TOKEN" "")
assert_ok "Seller orders" "$(get_code "$RESP")"

# ============================================================
echo -e "\n${YELLOW}[Phase 7] Payment & Delivery Flow${NC}"

echo "--- 7.1 Buyer pays ---"
RESP=$(api POST "/order/$ORDER_ID/pay" "$BUYER_TOKEN" '{"paymentMethod":"ALIPAY"}')
assert_ok "Pay order" "$(get_code "$RESP")"

echo "--- 7.2 Seller ships ---"
RESP=$(api POST "/order/$ORDER_ID/ship" "$SELLER_TOKEN" "")
assert_ok "Ship order" "$(get_code "$RESP")"

echo "--- 7.3 Buyer completes ---"
RESP=$(api POST "/order/$ORDER_ID/complete" "$BUYER_TOKEN" "")
assert_ok "Complete order" "$(get_code "$RESP")"

# ============================================================
echo -e "\n${YELLOW}[Phase 8] User Management (Admin)${NC}"

echo "--- 8.1 Admin list users ---"
RESP=$(api GET /user/list "$ADMIN_TOKEN" "")
assert_ok "List users" "$(get_code "$RESP")"

echo "--- 8.2 Admin update user ---"
RESP=$(api PUT "/user/$BUYER_ID" "$ADMIN_TOKEN" "{\"nickname\":\"Buyer Li VIP\",\"status\":1}")
assert_ok "Admin update user" "$(get_code "$RESP")"

echo "--- 8.3 View operation logs ---"
RESP=$(api GET "/admin/logs?limit=5" "$ADMIN_TOKEN" "")
assert_ok "View logs" "$(get_code "$RESP")"

# ============================================================
echo -e "\n${YELLOW}[Phase 9] System Roles & Permissions${NC}"

echo "--- 9.1 List roles ---"
RESP=$(api GET /system/roles "$ADMIN_TOKEN" "")
assert_ok "List roles" "$(get_code "$RESP")"

echo "--- 9.2 List permissions ---"
RESP=$(api GET /system/permissions "$ADMIN_TOKEN" "")
assert_ok "List permissions" "$(get_code "$RESP")"

echo "--- 9.3 View user role assignments ---"
RESP=$(api GET "/system/user-roles?userId=1" "$ADMIN_TOKEN" "")
assert_ok "User roles" "$(get_code "$RESP")"

# ============================================================
echo -e "\n${YELLOW}[Phase 10] Security Tests${NC}"

echo "--- 10.1 Fake uid: token rejected ---"
RESP=$(api GET /user/me "uid:1" "")
assert_rejected "Fake token" "$(get_code "$RESP")"

echo "--- 10.2 Tampered JWT rejected ---"
RESP=$(api GET /user/me "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIn0.badsignature" "")
assert_rejected "Tampered JWT" "$(get_code "$RESP")"

echo "--- 10.3 XSS payload accepted (frontend handles escaping) ---"
RESP=$(api POST /user/register "" "{\"username\":\"xsstest${TS}\",\"password\":\"test123456\",\"nickname\":\"<script>alert(1)</script>\"}")
assert_ok "XSS stored" "$(get_code "$RESP")"

echo "--- 10.4 WebSocket endpoint accessible ---"
WS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/ws)
assert_eq "WS endpoint" "200" "$WS_CODE"

# ============================================================
echo -e "\n============================================================"
echo -e "  ${GREEN}Passed: $PASS${NC}  |  ${RED}Failed: $FAIL${NC}"
echo "============================================================"
if [ "$FAIL" -gt 0 ]; then
    echo -e "${RED}FAILED${NC}"
    exit 1
else
    echo -e "${GREEN}ALL TESTS PASSED${NC}"
    exit 0
fi
